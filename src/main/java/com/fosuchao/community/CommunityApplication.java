package com.fosuchao.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void resolveConflict() {
        // 解决NettyRuntime冲突的问题，redis默认启动了netty，而es需要启动
        // Netty4Utils.setAvailableProcessors()方法中
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
