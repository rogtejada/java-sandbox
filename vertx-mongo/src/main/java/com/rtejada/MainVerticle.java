package com.rtejada;


import com.rtejada.employee.EmployeeResource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

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
    EmployeeResource employeeResource = EmployeeResource.getInstance();
    router.route("/assets/*").handler(StaticHandler.create("assets"));
    router.get("/api/employees").handler(employeeResource::getAll);
    router.route("/api/employees*").handler(BodyHandler.create());
    router.post("/api/employees").handler(employeeResource::add);
    router.get("/api/employees/:id").handler(employeeResource::getOne);
    router.delete("/api/employees/:id").handler(employeeResource::deleteOne);

    EmployeeResource.getInstance().connect(vertx);
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }
}
