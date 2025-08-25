package com.threadly.notification.adapter.redis;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = {
        RedisModule.class
    }
)
public class RedisTestApplication {

}
