package com.fosuchao.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 16:19
 */
@Configuration
public class DateFormatConfig {

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
