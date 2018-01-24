package com.aow.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        // Allow events for the designated addresses in/out of the event bus bridge
        BridgeOptions opts = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress("feed"));

        // Create the event bus bridge and add it to the router.
        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);

        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());
        //CorsHandler corsHandler = CorsHandler.create("https://localhost:8442");

        //corsHandler.allowedMethod(HttpMethod.GET);
//        corsHandler.allowedMethod(HttpMethod.POST);
//        corsHandler.allowedMethod(HttpMethod.PUT);
        //corsHandler.allowedMethod(HttpMethod.DELETE);
//        corsHandler.allowedHeader("Authorization");
//        corsHandler.allowedHeader("Content-Type");
//        corsHandler.allowedHeader("Access-Control-Allow-Origin");
//        corsHandler.allowedHeader("Access-Control-Allow-Headers");

        //router.route().handler(corsHandler);

        EventBus eb = vertx.eventBus();

        vertx.setPeriodic(1000l, t -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));

            eb.send("feed", new JsonObject().put("now", timestamp));
        });

        SelfSignedCertificate certificate = SelfSignedCertificate.create();
        vertx.createHttpServer(
                new HttpServerOptions()
                        .setSsl(true)
                        .setKeyCertOptions(certificate.keyCertOptions())
                        .setTrustOptions(certificate.trustOptions())
        )
                .requestHandler(router::accept)
                .listen(8443);
    }
}
