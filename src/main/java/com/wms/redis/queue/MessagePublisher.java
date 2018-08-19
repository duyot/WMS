package com.wms.redis.queue;

public interface MessagePublisher {

    void publish(final String message);
}
