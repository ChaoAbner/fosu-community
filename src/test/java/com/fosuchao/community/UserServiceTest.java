package com.fosuchao.community;

import com.fosuchao.community.entity.DiscussPost;
import com.fosuchao.community.entity.User;
import com.fosuchao.community.service.DiscussPostService;
import com.fosuchao.community.service.UserService;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 16:08
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @Test
    public void discussPostSelect() {
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPosts(0, 1, 10, 0);
        System.out.println(discussPosts);
    }

    @Test
    public void userSelect() {
        User user = userService.selectById(111);
        System.out.println(user);
    }
}
