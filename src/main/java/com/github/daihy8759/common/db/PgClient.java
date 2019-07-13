package com.github.daihy8759.common.db;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.sqlclient.Pool;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PgClient {

    String DEFAULT_POOL_NAME = "DEFAULT_POSTGRESQL_POOL";

    public static Pool createShared(Vertx vertx, JsonObject config) {
        return ClientHelper.getOrCreate(vertx, config, DEFAULT_POOL_NAME);
    }
}
