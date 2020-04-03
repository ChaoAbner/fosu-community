package com.fosuchao.community.service;

import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.dao.LoginTicketMapper;
import com.fosuchao.community.dao.UserMapper;
import com.fosuchao.community.entity.LoginTicket;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.utils.CommunityUtil;
import com.fosuchao.community.utils.MailUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 15:29
 */

@Service
public class UserService implements CommunityConstant{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailUtil mailUtil;

    @Value("${community.path.domain}")
    private String domain;


    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    public User selectByName(String name) {
        return userMapper.selectByName(name);
    }

    public User selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    public int updateStatus(int id, int status) {
        return userMapper.updateStatus(id, status);
    }

    public int updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password);
    }

    public int updateHeaderUrl(int id, String headerUrl) {
        return userMapper.updateHeaderUrl(id, headerUrl);
    }

    // 用户注册
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 判断参数是否合法
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 空值判断
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证账号
        User checkUser = userMapper.selectByName(user.getUsername());
        if (checkUser != null) {
            map.put("usernameMsg", "账号已被注册了！");
            return map;
        }

        // 验证邮箱
        checkUser = userMapper.selectByEmail(user.getEmail());
        if (checkUser != null) {
            map.put("emailMsg", "邮箱已被注册了！");
            return map;
        }

        // 注册账号
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setSalt(CommunityUtil.uuid().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setCreateTime(new Date());
        user.setActivationCode(CommunityUtil.uuid());

        userMapper.insertUser(user);
        // 激活邮件
        // 格式：http://localhost:8899/activation/111/code
        StringBuilder url = new StringBuilder();
        url.append(domain).append("/activation/").
                append(user.getId()).append("/").append(user.getActivationCode());

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("url", url.toString());

        String content = templateEngine.process("mail/activation", context);
        mailUtil.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            // 重复激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            // 激活用户
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            // 激活失败
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expired) {
        Map<String, Object> map = new HashMap<>();
        // 判断非空
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            // 用户不存在
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }
        // 比较密码
        String currPwd = CommunityUtil.md5(password + user.getSalt());
        if (!currPwd.equals(user.getPassword())) {
            // 密码不正确
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        // 验证通过,生成ticket
        LoginTicket ticket = new LoginTicket();
        ticket.setExpired(new Date(System.currentTimeMillis() + expired * 1000));
        ticket.setUserId(user.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtil.uuid());
        loginTicketMapper.insertTicket(ticket);

        map.put("ticket", ticket.getTicket());

        return map;
    }

    public LoginTicket selectByTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }
}