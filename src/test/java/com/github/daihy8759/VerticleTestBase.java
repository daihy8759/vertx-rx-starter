package com.github.daihy8759;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;

/**
 * VerticleTestBase
 */
@ExtendWith(VertxExtension.class)
public class VerticleTestBase {

    Vertx vertx;
    JsonObject config;

    @BeforeEach
    void prepare() {
        vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(1000).setPreferNativeTransport(true));
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("local.json").getFile());
        Buffer confBuffer = vertx.fileSystem().readFileBlocking(file.getAbsolutePath());
        config = new JsonObject(confBuffer);
    }

    @AfterEach
    void cleanup() {
        vertx.close();
    }
}