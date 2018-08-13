package com.wms.redis.repository;

import com.wms.redis.model.AuthTokenInfo;
import com.wms.redis.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class RedisRepositoryImpl implements RedisRepository {
    private static final String KEY = "tokenInfor";
    
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;
    
    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }
    
    public void add(final Token token) {
        if (token.getTokenInfor() == null){
            return ;
        }
        hashOperations.put(KEY, token.getClientIp(), token.getTokenInfor());
    }

    public void delete(final String id) {
        hashOperations.delete(KEY, id);
    }
    
    public AuthTokenInfo find(final String id){
        return (AuthTokenInfo) hashOperations.get(KEY, id);
    }
    
    public Map<Object, Object> findAll(){
        return hashOperations.entries(KEY);
    }

  
}
