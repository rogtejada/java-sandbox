package com.rtejada;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainVerticle extends AbstractVerticle {

  private PgPool client;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());


  }

  @Override
  public void start() {
    Router router = Router.router(vertx);

    router.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response
          .putHeader("content-type", "text/html")
          .end("Up");
    });
    router.get("/api/employees").handler(this::getAll);
    router.route("/api/employees*").handler(BodyHandler.create());
    router.post("/api/employees").handler(this::add);
    router.get("/api/employees/:id").handler(this::getOne);
    router.delete("/api/employees/:id").handler(this::deleteOne);

    PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(5433)
        .setHost("localhost")
        .setDatabase("test")
        .setUser("postgres")
        .setPassword("password");
    PoolOptions poolOptions = new PoolOptions()
        .setMaxSize(5);
    client = PgPool.pool(vertx, connectOptions, poolOptions);
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }


  @Override
  public void stop() {
    client.close();
  }

  private void add(RoutingContext routingContext) {
    final Employee employee = Json.decodeValue(routingContext.getBodyAsString(),
        Employee.class);
    String sql = "INSERT INTO employee (id, name, email) values (':id', ':name', ':email')";
    sql = sql.replace(":id", UUID.randomUUID().toString())
        .replace(":name", employee.getName())
        .replace(":email", employee.getEmail());

    client.query(sql).execute(ar -> {
      if (ar.succeeded()) {
        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end();
      }
    });
  }

  private void getOne(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      String sql = "SELECT * FROM employee WHERE id = ':id'";
      client
          .query(sql.replace(":id", id))
          .execute(ar -> {
            if (ar.succeeded()) {
              RowSet<Row> rs = ar.result();
              Row row = rs.iterator().next();
              routingContext.response()
                  .putHeader("content-type", "application/json; charset=utf-8")
                  .end(Json.encodePrettily(new Employee(row.getUUID("id"), row.getString("name"),
                      row.getString("email"))));
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
      String sql = "DELETE FROM employee WHERE id = ':id'";
      client.query(sql.replace(":id", id)).execute(
          ar ->
              routingContext.response().setStatusCode(204).end()
      );
    }
  }

  private void getAll(RoutingContext routingContext) {
    client
        .query("SELECT * FROM employee")
        .execute(ar -> {
          if (ar.succeeded()) {
            RowSet<Row> rs = ar.result();
            List<Employee> result = new ArrayList<>(rs.size());

            rs.iterator().forEachRemaining(row ->
                result.add(new Employee(row.getUUID("id"), row.getString("name"),
                    row.getString("email")))
            );
            routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(result));
          } else {
            routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(new ArrayList<>()));
          }
        });
  }
}
