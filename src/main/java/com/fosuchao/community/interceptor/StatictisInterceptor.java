package com.fosuchao.community.interceptor;

import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.StatisticService;
import com.fosuchao.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 统计UV和DAU
 * @author: Joker Ye
 * @create: 2020/4/6 21:37
 */
@Component
public class StatictisInterceptor implements HandlerInterceptor {

    @Autowired
    StatisticService statisticService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 统计UV
        String ip = request.getRemoteHost();
        statisticService.setUV(ip);

        // 统计DAU，只统计登录用户
        User user = hostHolder.getUser();
        if (user != null) {
            statisticService.setDAU(user.getId());
        }
        return true;
    }
}
