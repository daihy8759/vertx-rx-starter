package com.github.daihy8759;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.daihy8759.common.util.response.ApiResponse;
import com.github.daihy8759.common.util.response.ConstantCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

/**
 * MainVerticleTest
 */
@DisplayName("⭐️MainVerticle Test")
public class MainVerticleTest extends VerticleTestBase {

    @Test
    @DisplayName("Deploy MainVerticle")
    void deployMainVerticle(VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), new DeploymentOptions().setConfig(config),
                testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    @DisplayName("Test Get Token with empty param")
    public void testGetTokenEmptyParam(Vertx vertx, VertxTestContext testContext) {
        WebClient webClient = WebClient.create(vertx);
        Checkpoint deploymentCheckpoint = testContext.checkpoint();
        Checkpoint requestCheckpoint = testContext.checkpoint(10);

        vertx.deployVerticle(new MainVerticle(), new DeploymentOptions().setConfig(config),
                testContext.succeeding(id -> {
                    deploymentCheckpoint.flag();

                    webClient.get(config.getInteger("api.http.port"), "localhost", "/api/v1/token")
                            .as(BodyCodec.string()).send(testContext.succeeding(resp -> {
                                testContext.verify(() -> {
                                    assertThat(resp.statusCode()).isEqualTo(200);
                                    assertThat(resp.body()).contains(
                                            ApiResponse.fail().setRetCode(ConstantCode.TOKEN_REQUIRE_FAIL).toString());
                                    requestCheckpoint.flag();
                                });
                            }));
                }));
    }

}