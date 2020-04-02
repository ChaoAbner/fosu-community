package com.fosuchao.community.dao;

import com.fosuchao.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 15:25
 */

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updatePassword(@Param("id") int id, @Param("password") String password);

    int updateHeaderUrl(@Param("id") int id, @Param("headerUrl") String headerUrl);
}
