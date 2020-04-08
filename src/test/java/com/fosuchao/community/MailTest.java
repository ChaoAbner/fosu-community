package com.fosuchao.community;

import com.fosuchao.community.utils.MailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/5 21:17
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class MailTest {

    @Autowired
    MailUtil mailUtil;

    @Test
    public void mailTest() {
        mailUtil.sendCompanyMail("2473109110@qq.com", "主题", "hahhahahah");
    }

}
