package in.action.poster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    List<Future> futures = List.of("in.action.poster.verticle.HttpVerticle")
      .stream().map(this::deployVerticle).collect(Collectors.toList());
    CompositeFuture.all(futures)
      .setHandler(f ->{
        if (f.succeeded()) {
          startFuture.complete();
        }else{
          startFuture.fail(f.cause());
        }
      });
  }

  Future<Void> deployVerticle(String verticleName) {
    Future<Void> retVal = Future.future();
    vertx.deployVerticle(verticleName, event -> {
      if (event.succeeded()) {
        retVal.complete();
      } else {
        retVal.fail(event.cause());
      }
    });
    return retVal;
  }
}
