package com.fosuchao.community;

import com.fosuchao.community.service.LikeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 18:46
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {

    @Autowired
    LikeService likeService;

    @Test
    public void likeTest() {
        System.out.println(likeService.getEntityLikeCount(2, 237));
    }
}
