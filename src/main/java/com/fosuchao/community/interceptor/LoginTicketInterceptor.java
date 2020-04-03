package com.fosuchao.community.interceptor;

import com.fosuchao.community.dao.LoginTicketMapper;
import com.fosuchao.community.entity.LoginTicket;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.UserService;
import com.fosuchao.community.utils.CookieUtil;
import com.fosuchao.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @description: 登录态拦截器，判断ticket是否有效
 * @author: Joker Ye
 * @create: 2020/4/2 22:58
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue("ticket", request);
        if (ticket != null) {
            // 查询凭证
            LoginTicket currTicket = userService.selectByTicket(ticket);
            // 验证凭证
            if (currTicket != null && currTicket.getExpired().after(new Date()) && currTicket.getStatus() == 0) {
                // 查找并设置当前用户
                User user = userService.selectById(currTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        // 在模板中设置持有用户
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 清除持有用户
        hostHolder.clear();
    }
}
