package in.action.poster.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class NoopUserVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {


    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer("noop-user-vertx");


    consumer.handler(message -> {

      var body = message.body();


      Map<String, Object> map = body.getMap();

      log.debug("receive body: {}", map);



      map.put("noopid", UUID.randomUUID());

      message.reply(Json.encodePrettily(map));

    });

    startFuture.complete();

  }

}
