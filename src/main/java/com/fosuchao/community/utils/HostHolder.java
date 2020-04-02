package com.fosuchao.community.utils;

import com.fosuchao.community.entity.User;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Component;

/**
 * @description: 使用ThreadLocal持有用户信息，用于代替session对象
 * @author: Joker Ye
 * @create: 2020/4/2 22:48
 */
@Component
public class HostHolder {

    private ThreadLocal<User> user = new ThreadLocal<User>();

    public void setUser(User curr) {
        user.set(curr);
    }

    public User getUser() {
        return user.get();
    }

    public void clear() {
        user.remove();
    }
}
