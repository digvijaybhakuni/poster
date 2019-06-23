package in.action.poster.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    var baseRouter = Router.router(vertx);
    var apiRouter = Router.router(vertx);

    baseRoute(baseRouter);
    apiRoute(apiRouter);
    baseRouter.mountSubRouter("/api", apiRouter);

    vertx.createHttpServer().requestHandler(baseRouter).listen(9990, result -> {
      if (result.succeeded()) {
        log.debug("HttpVerticle is deployed");
        startFuture.complete();
      } else {
        startFuture.fail(result.cause());
      }
    });
  }

  private void baseRoute(Router router) {
    router.route("/").handler(routingContext -> {
      var response = routingContext.response();
      response.putHeader("content-type", "text/plain").end("Hello Vert.x!");
    });
  }

  private void apiRoute(Router router) {
    router.route("/user*").handler(BodyHandler.create());
    router.get("/users").handler(this::getUsers);
  }

  private void getUsers(RoutingContext routingContext) {
    log.debug("GetUsers.....");
    var user = new JsonObject()
      .put("password", "th!$p@ss")
      .put("username", "digvijaybhk@gmail.com");

    vertx.eventBus().send("noop-user-vertx", user, ar -> {

      if (ar.succeeded()) {
        routingContext.response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(ar.result().body().toString());
      }else {
        log.debug("This a fall back");
        routingContext.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(user));
      }

    });



  }

}
