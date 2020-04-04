package com.fosuchao.community.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 16:01
 */
public class JsonResponseUtil {

    // 状态码 code
    // 响应信息 msg
    // 其它信息 map

    public static String getJsonResponse(int code) {
        return getJsonResponse(code, null, null);
    }

    public static String getJsonResponse(int code, String msg) {
        return getJsonResponse(code, msg, null);
    }

    public static String getJsonResponse(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String s : map.keySet()) {
                json.put(s, map.get(s));
            }
        }
        return json.toJSONString();
    }
}
