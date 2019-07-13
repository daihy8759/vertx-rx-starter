package com.github.daihy8759.verticle;

import java.util.UUID;

import com.github.daihy8759.common.db.PgClient;
import com.github.daihy8759.common.util.Constants;
import com.github.daihy8759.common.util.EventBusUtil;
import com.github.daihy8759.common.util.response.ApiResponse;
import com.github.daihy8759.common.util.response.ConstantCode;
import com.github.daihy8759.keys.TokenKey;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.Single;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.reactivex.sqlclient.SqlClient;
import io.vertx.reactivex.sqlclient.Tuple;

public class TokenVerticle extends AbstractVerticle {

    private SqlClient client;
    private RedisClient redisClient;
    private JsonObject constantConfig;

    @Override
    public void start(Promise<Void> promise) {
        // init
        client = PgClient.createShared(vertx, config());

        redisClient = RedisClient.create(vertx, config().getJsonObject("redis"));
        constantConfig = config().getJsonObject("constant");
        // create handler
        vertx.eventBus().consumer(TokenKey.GET_TOKEN, this::getToken);
        promise.complete();
    }

    private void getToken(Message<JsonObject> message) {
        JsonObject paramObject = message.body().getJsonObject(Constants.REQUEST_PARAM);
        String appId = paramObject.getString("appId");
        String appSecret = paramObject.getString("appSecret");
        if (StringUtils.isAnyBlank(appId, appSecret)) {
            message.reply(ApiResponse.fail().setRetCode(ConstantCode.TOKEN_REQUIRE_FAIL).toString());
            return;
        }
        exists(appId, appSecret).flatMap(r -> {
            if (r) {
                return createToken(appId);
            } else {
                return Single.just(ApiResponse.fail().setRetCode(ConstantCode.TOKEN_REQUIRE_FAIL));
            }
        }).subscribe(r -> {
            message.reply(r);
        }, e -> EventBusUtil.catchException(e, message));
    }

    private Single<ApiResponse> createToken(String appId) {
        long expiresIn = constantConfig.getLong("tokenExpire", 1200L);
        String token = UUID.randomUUID().toString().replace("-", "");
        return redisClient.rxSetex(token, expiresIn, appId).flatMap(r -> Single
                .just(ApiResponse.success().setData(new JsonObject().put("token", token).put("expiresIn", expiresIn))));
    }

    private Single<Boolean> exists(String appId, String appSecret) {
        return client.rxPreparedQuery("select 1 from tb_application where app_id=$1 and app_secret=$2",
                Tuple.of(appId, appSecret)).flatMap(rows -> Single.just(rows.size() > 0));
    }

}
