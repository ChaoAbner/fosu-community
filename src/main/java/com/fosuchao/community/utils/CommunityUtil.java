package com.fosuchao.community.utils;

import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
//import java.net.http.HttpRequest;
import java.util.UUID;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 19:40
 */
public class CommunityUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static boolean isAjax(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            return true;
        }
        return false;
    }
}
