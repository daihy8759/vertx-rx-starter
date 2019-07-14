package com.github.daihy8759.service;

import io.reactivex.Maybe;
import io.vertx.reactivex.redis.RedisClient;

/**
 * TokenService
 */
public class TokenService {

    private RedisClient redisClient;

    public TokenService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public Maybe<String> getToken(String token) {
        return redisClient.rxGet(token);
    }

}
