package com.fosuchao.community.dao;


import com.fosuchao.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * 这里使用注解的方式来设置SQL，优点：方便， 缺点：可读性差
 * 不建议在使用这个方法来保存登录凭证，已优化使用缓存方式。
 */
@Deprecated
@Mapper
public interface LoginTicketMapper {

    // 获取用户的ticket
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 插入ticket
    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) ",
            "values (#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTicket(LoginTicket ticket);

    // 更新状态status
    @Update({
            "update login_ticket set status = #{status} where ticket=#{ticket}"
    })
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);

}
