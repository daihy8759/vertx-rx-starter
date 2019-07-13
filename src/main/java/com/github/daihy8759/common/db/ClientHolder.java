package com.github.daihy8759.common.db;

import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class ClientHolder implements Shareable {

    private final Vertx vertx;
    private final JsonObject config;
    private final Runnable closeRunner;

    private Pool client;
    private int refCount = 1;

    ClientHolder(Vertx vertx, JsonObject config, Runnable closeRunner) {
        this.vertx = vertx;
        this.config = config;
        this.closeRunner = closeRunner;
    }

    synchronized Pool client() {
        if (client == null) {
            client = createPool(vertx, config);
        }
        return client;
    }

    private static Pool createPool(Vertx vertx, JsonObject config) {
        JsonObject databaseConfig = config.getJsonObject("database");
        PgConnectOptions connectOptions = new PgConnectOptions().setHost(databaseConfig.getString("host"))
                .setPort(databaseConfig.getInteger("port")).setDatabase(databaseConfig.getString("database"))
                .setUser(databaseConfig.getString("username")).setPassword(databaseConfig.getString("password"));
        PoolOptions poolOptions = new PoolOptions().setMaxSize(databaseConfig.getInteger("maxPoolSize"));
        return PgPool.pool(vertx, connectOptions, poolOptions);
    }

    synchronized void incRefCount() {
        refCount++;
    }

    synchronized void close() {
        if (--refCount == 0) {
            if (client != null) {
                client.close();
            }
            if (closeRunner != null) {
                closeRunner.run();
            }
        }
    }
}
