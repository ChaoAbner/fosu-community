package com.fosuchao.community.aop.advice;

import com.fosuchao.community.utils.CommunityUtil;
import com.fosuchao.community.utils.JsonResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description: 统一异常管理
 * @author: Joker Ye
 * @create: 2020/4/4 11:17
 */

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(Exception.class);

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error(String.format("服务器发生异常: %s", e.getMessage()));

        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        if (CommunityUtil.isAjax(request)) {
            // ajax请求异常处理
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JsonResponseUtil.getJsonResponse(500, "服务器异常"));
        } else {
            // 模板异常处理,返回错误模板
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
