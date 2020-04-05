package com.fosuchao.community.interceptor;

import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.MessageService;
import com.fosuchao.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/5 15:54
 */

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
            int conversationUnReadCount = messageService.selectConversationUnReadCount(null, user.getId());

            modelAndView.addObject("allUnreadCount", noticeUnreadCount + conversationUnReadCount);
        }
    }
}
