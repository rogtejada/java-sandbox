package com.rtejada;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  public static final String COLLECTION = "employees";
  private MongoClient mongo;

  @Override
  public void start(Future<Void> fut) {

    mongo = MongoClient.createShared(vertx, config());

    startWebApp((http) -> {
      if (http.succeeded()) {
        fut.complete();
      } else {
        fut.fail(http.cause());
      }
    });
  }

  private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
    Router router = Router.router(vertx);

    router.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response
          .putHeader("content-type", "text/html")
          .end("Up");
    });

    router.route("/assets/*").handler(StaticHandler.create("assets"));

    router.get("/api/employees").handler(this::getAll);
    router.route("/api/employees*").handler(BodyHandler.create());
    router.post("/api/employees").handler(this::add);
    router.get("/api/employees/:id").handler(this::getOne);
    router.delete("/api/employees/:id").handler(this::deleteOne);

    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(8080);
  }

  @Override
  public void stop() {
    mongo.close();
  }

  private void add(RoutingContext routingContext) {
    final Employee employee = Json.decodeValue(routingContext.getBodyAsString(),
        Employee.class);

    mongo.insert(COLLECTION, employee.toJson(), r ->
        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(employee.setId(r.result()))));
  }

  private void getOne(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.findOne(COLLECTION, new JsonObject().put("_id", id), null, ar -> {
        if (ar.succeeded()) {
          if (ar.result() == null) {
            routingContext.response().setStatusCode(404).end();
            return;
          }
          Employee employee = new Employee(ar.result());
          routingContext.response()
              .setStatusCode(200)
              .putHeader("content-type", "application/json; charset=utf-8")
              .end(Json.encodePrettily(employee));
        } else {
          routingContext.response().setStatusCode(404).end();
        }
      });
    }
  }

  private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.removeOne(COLLECTION, new JsonObject().put("_id", id),
          ar -> routingContext.response().setStatusCode(204).end());
    }
  }

  private void getAll(RoutingContext routingContext) {
    mongo.find(COLLECTION, new JsonObject(), results -> {
      List<JsonObject> objects = results.result();
      List<Employee> employees = objects.stream().map(Employee::new).collect(Collectors.toList());
      routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(employees));
    });
  }
}
