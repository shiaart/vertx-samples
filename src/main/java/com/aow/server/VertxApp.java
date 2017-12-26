package com.aow.server;

import io.vertx.core.Vertx;

public class VertxApp {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

}
