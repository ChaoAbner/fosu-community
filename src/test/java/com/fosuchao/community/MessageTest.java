package com.fosuchao.community;

import com.fosuchao.community.dao.MessageMapper;
import com.fosuchao.community.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 20:47
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageTest {

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void messageTest() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 10);
        System.out.println(messages);
        int count = messageMapper.selectConversationsCount(111);
        System.out.println(count);
        count = messageMapper.selectConversationUnReadCount("111_112", 111);
        System.out.println(count);
        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        System.out.println(messages1);
        count = messageMapper.selectLettersCount("111_112");
        System.out.println(count);

    }
}
