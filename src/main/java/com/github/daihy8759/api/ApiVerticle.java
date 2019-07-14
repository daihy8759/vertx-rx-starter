package com.github.daihy8759.api;

import com.github.daihy8759.common.util.Constants;
import com.github.daihy8759.common.util.response.ApiResponse;
import com.github.daihy8759.common.util.response.ConstantCode;
import com.github.daihy8759.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.redis.RedisClient;
import lombok.extern.log4j.Log4j2;

/**
 * ApiVerticle
 */
@Log4j2
public class ApiVerticle extends AbstractVerticle {

    private static final int DEFAULT_PORT = 8787;

    private TokenService tokenService;
    private JsonArray NO_AUTH_URL;

    @Override
    public Completable rxStart() {
        JsonObject apiObject = config().getJsonObject("api");
        NO_AUTH_URL = apiObject.getJsonArray("noAuthUrl");
        RedisClient redisClient = RedisClient.create(vertx, config().getJsonObject("redis"));
        this.tokenService = new TokenService(redisClient);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/api/*").handler(this::handleApi);

        String host = config().getString("api.http.address", "localhost");
        int port = config().getInteger("api.http.port", DEFAULT_PORT);
        return vertx.createHttpServer().requestHandler(router).rxListen(port, host).ignoreElement();
    }

    private void handleApi(RoutingContext routingContext) {
        String requestPath = routingContext.request().path();
        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE,
                "application/json;charset=UTF-8");
        if (requestPath.length() < 7) {
            routingContext.response()
                    .end(ApiResponse.fail().setMessage("handler not exists!").toString());
            return;
        }
        String apiVersion = requestPath.substring(5, 7);
        String eventBusKey = requestPath.substring(7);

        if (NO_AUTH_URL.contains(eventBusKey)) {
            sendEvent(routingContext, eventBusKey, apiVersion);
        } else {
            checkToken(routingContext.request()).subscribe(r -> {
                if (r) {
                    sendEvent(routingContext, eventBusKey, apiVersion);
                } else {
                    routingContext.response().end(
                            ApiResponse.fail().setRetCode(ConstantCode.TOKEN_NOT_FOUND).toString());
                }
            });
        }
    }

    private void sendEvent(RoutingContext routingContext, String eventBusKey, String apiVersion) {
        HttpServerResponse response = routingContext.response();
        vertx.eventBus()
                .rxRequest(eventBusKey,
                        parseParam(routingContext,
                                new JsonObject().put(Constants.API_VERSION, apiVersion)))
                .subscribe(res -> response.end(res.body().toString()), e -> response
                        .end(ApiResponse.fail().setMessage(e.getMessage()).toString()));
    }

    private String getToken(HttpServerRequest request) {
        String token = request.getHeader(Constants.AUTH_HEADER);
        log.debug("token: {}", token);
        if (StringUtils.isBlank(token)) {
            return "";
        }
        return StringUtils.trim(token.replace(":", ""));
    }

    private Single<Boolean> checkToken(HttpServerRequest request) {
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            return Single.just(false);
        }
        return tokenService.getToken(token).defaultIfEmpty("").map(StringUtils::isNotBlank)
                .toSingle();
    }

    private JsonObject parseParam(RoutingContext routingContext, JsonObject extParam) {
        MultiMap paramsMap = routingContext.request().params();
        JsonObject paramObject = new JsonObject();
        extParam.put(Constants.TOKEN, getToken(routingContext.request()));
        extParam.put(Constants.REQUEST_PARAM, paramObject);
        paramsMap.entries().forEach(t -> paramObject.put(t.getKey(), t.getValue()));
        String contentType = routingContext.request().getHeader("Content-Type");
        if (StringUtils.isNotBlank(contentType) && contentType.startsWith("application/json")) {
            extParam.put(Constants.REQUEST_BODY, routingContext.getBodyAsJson());
        }
        return extParam;
    }
}
