package com.fosuchao.community;

import com.fosuchao.community.utils.SensitiveFilterUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 11:25
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SensitiveTest {
    @Autowired
    SensitiveFilterUtil filterUtil;

    @Test
    public void filter() {
        String word = "书书书色情123色情";
        String filter = filterUtil.filter(word);
        System.out.println(filter);

        filter = filterUtil.filter("这里可以☆赌☆博☆,可以☆嫖☆娼☆,可以☆吸☆毒☆,可以☆开☆票☆,哈哈哈!");
        System.out.println(filter);
    }
}
