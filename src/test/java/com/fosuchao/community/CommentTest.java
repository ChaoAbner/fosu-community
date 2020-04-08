package com.fosuchao.community;

import com.fosuchao.community.dao.CommentMapper;
import com.fosuchao.community.entity.Comment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/3 16:34
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class CommentTest {

    @Autowired
    CommentMapper commentMapper;

    @Test
    public void comment() {
        List<Comment> comments =
                commentMapper.selectCommentsByEntity(1, 228, 0, 5);
        for (Comment comment : comments) {
            System.out.println(comment);
        }

        System.out.println("---");

        System.out.println(commentMapper.selectCountByEntity(228, 1));

    }
}
