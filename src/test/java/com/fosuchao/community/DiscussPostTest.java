package com.fosuchao.community;

import com.fosuchao.community.dao.DiscussPostMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 15:53
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class DiscussPostTest {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void discussPosttest() {
        System.out.println(discussPostMapper.selectDiscussPostById(109));
        System.out.println(discussPostMapper.updateCommentCount(109, 100));

    }
}
