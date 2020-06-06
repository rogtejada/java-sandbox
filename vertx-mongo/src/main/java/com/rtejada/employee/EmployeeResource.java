package com.rtejada.employee;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeResource {

  public static final String COLLECTION = "employees";
  private static EmployeeResource instance = null;
  private MongoClient mongo;

  private EmployeeResource() {
  }

  public static synchronized EmployeeResource getInstance() {
    if(null == instance) {
      instance = new EmployeeResource();
    }
    return instance;
  }

  public void connect(Vertx vertx) {
    JsonObject config = new JsonObject();
    config.put("db_name", COLLECTION);
    config.put("connection_string", "mongodb://localhost:27017");
    mongo = MongoClient.createShared(vertx, config);
  }

  public void add(RoutingContext routingContext) {
    final Employee employee = Json.decodeValue(routingContext.getBodyAsString(),
        Employee.class);

    mongo.insert(COLLECTION, employee.toJson(), r ->
        routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(employee.setId(r.result()))));
  }

  public void getOne(RoutingContext routingContext) {
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

  public void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.removeOne(COLLECTION, new JsonObject().put("_id", id),
          ar -> routingContext.response().setStatusCode(204).end());
    }
  }


  public void getAll(RoutingContext routingContext) {
    mongo.find(COLLECTION, new JsonObject(), results -> {
      List<JsonObject> objects = results.result();
      List<Employee> employees = objects.stream().map(Employee::new).collect(Collectors.toList());
      routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(employees));
    });
  }

}
