//package com.wms.sercurity;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//
//import java.time.Duration;
//
//@EnableRedisHttpSession
//public class RedisSessionConfig {
//
//    @Bean
//    public LettuceConnectionFactory connectionFactory() {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .commandTimeout(Duration.ofSeconds(100))
//                .shutdownTimeout(Duration.ZERO)
//                .build();
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("45.32.103.51", 6379), clientConfig);
//    }
//
//}