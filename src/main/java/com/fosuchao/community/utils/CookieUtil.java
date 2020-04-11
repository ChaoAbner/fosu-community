package com.fosuchao.community.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
//import java.net.http.HttpRequest;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 23:00
 */
public class CookieUtil {

    public static String getValue(String key, HttpServletRequest request) {

        if (StringUtils.isBlank(key) || request == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
