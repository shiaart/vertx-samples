package com.aow.server;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
              req.response()
                .putHeader("content-type", "text/plain")
                .end("Hello from Vert.xxxxxxxxxxx!");
            }).listen(8080);
        System.out.println("HTTP server started on port 8080");
    }
}
