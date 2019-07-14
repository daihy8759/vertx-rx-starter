package com.github.daihy8759;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.daihy8759.api.ApiVerticle;
import com.github.daihy8759.common.util.response.ApiMessageCodec;
import com.github.daihy8759.common.util.response.ApiResponse;
import com.github.daihy8759.verticle.TokenVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import lombok.extern.log4j.Log4j2;

/**
 * MainVerticle
 */
@Log4j2
public class MainVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        vertx.exceptionHandler(log::error);
        EventBus eventBus = vertx.eventBus();
        eventBus.getDelegate().registerDefaultCodec(ApiResponse.class, new ApiMessageCodec());
        // Json.mapper.registerModule(new JavaTimeModule());
        Json.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Json.mapper.setSerializationInclusion(Include.NON_NULL);

        List<Single<String>> vehicleList = new ArrayList<>();
        vehicleList.add(deploy(ApiVerticle.class));
        vehicleList.add(deploy(TokenVerticle.class));
        return Single.concat(vehicleList).ignoreElements();
    }

    private <T> Single<String> deploy(Class<T> clazz) {
        return vertx.rxDeployVerticle(clazz.getName(), new DeploymentOptions().setConfig(config()));
    }
}
