package com.wms.redis.repository;

import com.wms.redis.model.AuthTokenInfo;
import com.wms.redis.model.Token;

import java.util.Map;

public interface RedisRepository {

    /**
     * Return all movies
     */
    Map<Object, Object> findAll();

    /**
     * Add key-value pair to Redis.
     */
    void add(Token movie);

    /**
     * Delete a key-value pair in Redis.
     */
    void delete(String id);
    
    /**
     * find a movie
     */
    AuthTokenInfo find(String id);
    
}
