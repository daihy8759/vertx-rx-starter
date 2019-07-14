package com.github.daihy8759.common.db;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.shareddata.LocalMap;
import io.vertx.reactivex.sqlclient.Pool;

/**
 * ClientHepler
 */
public class ClientHelper {

    private static final String DS_LOCAL_MAP_NAME_BASE = "__vertx.MySQLPostgreSQL.pools.";

    public static Pool getOrCreate(Vertx vertx, JsonObject config, String poolName) {
        synchronized (vertx) {
            LocalMap<String, ClientHolder> map =
                    vertx.sharedData().getLocalMap(DS_LOCAL_MAP_NAME_BASE + "PostgreSQL");

            ClientHolder theHolder = map.get(poolName);
            if (theHolder == null) {
                theHolder =
                        new ClientHolder(vertx, config, () -> removeFromMap(vertx, map, poolName));
                map.put(poolName, theHolder);
            } else {
                theHolder.incRefCount();
            }
            return new ClientWrapper(theHolder);
        }
    }

    private static void removeFromMap(Vertx vertx, LocalMap<String, ClientHolder> map,
            String poolName) {
        synchronized (vertx) {
            map.remove(poolName);
            if (map.isEmpty()) {
                map.close();
            }
        }
    }
}
