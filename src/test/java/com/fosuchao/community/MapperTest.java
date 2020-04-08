package com.fosuchao.community;

import com.fosuchao.community.dao.LoginTicketMapper;
import com.fosuchao.community.dao.MessageMapper;
import com.fosuchao.community.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/4 10:36
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTest {

//    @Autowired
//    LoginTicketMapper loginTicketMapper;

    @Autowired
    MessageMapper messageMapper;

    @Test
    public void loginTicketTest() {
//        loginTicketMapper.updateStatus("3564ef2d73c84ef4b1916694c2e96b75", 1);
    }

    @Test
    public void messageTest() {
//        System.out.println(messageMapper.selectNoticeUnreadCount(111, null));
        System.out.println(messageMapper.selectLastestNotice(112, "like"));
//        System.out.println(messageMapper.selectNoticeCount(111, null));
        System.out.println(messageMapper.selectLastestNotice(112, "follow"));
        System.out.println(messageMapper.selectLastestNotice(112, "comment"));
    }

}
