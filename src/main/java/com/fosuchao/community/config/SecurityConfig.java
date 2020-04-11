package com.fosuchao.community.config;


import com.fosuchao.community.constant.CommunityConstant;
import com.fosuchao.community.utils.CommunityUtil;
import com.fosuchao.community.utils.JsonResponseUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description:
 * @author: Joker Ye
 * @create: 2020/4/2 19:19
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**
     * 最web请求处理
     * @Param [web]
     * @return void
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 忽略对静态资源的限制
        web.ignoring().mvcMatchers("/resources/**");
    }

    /**
     * 进行授权
     * @Param [http]
     * @return void
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置权限对应的可访问接口，即授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/header/**",
                        "/user/password",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                ).hasAnyAuthority(
                        AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
                        AUTHORITY_MODERATOR, AUTHORITY_ADMIN
                )
                .antMatchers(
                        "/discuss/delete",
                        "/admin/**",
                        "/statistic/**"
                ).hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                // 禁用csrf
                .and().csrf().disable();

        // 当权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException e) throws IOException, ServletException {
                        // 根据请求方式处理
                        if (CommunityUtil.isAjax(request)) {
                            // ajax
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JsonResponseUtil.getJsonResponse(400, "请先登录再进行操作哦"));
                        } else {
                            // 跳转登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response,
                                       AccessDeniedException e) throws IOException, ServletException {
                        // 根据请求方式处理
                        if (CommunityUtil.isAjax(request)) {
                            // ajax
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JsonResponseUtil.getJsonResponse(400, "没有访问此功能的权限!"));
                        } else {
                            // 跳转登录页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        http.logout().logoutUrl("/securitylogout");

    }
}
