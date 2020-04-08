package com.fosuchao.community.utils;

import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

/**
 * @description: 邮件工具
 * @author: Joker Ye
 * @create: 2020/4/2 19:52
 */
@Component
public class MailUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String account;

    @Value("${spring.mail.password}")
    private String pass;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.protocol}")
    private String protocol;

    /**
     * 发送普通邮件
     * @Param [to, subject, content]
     * @return void
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(account);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }
    }

    /**
     * 发送html邮件
     * @Param [to, subject, content]
     * @return void
     */
    public void sendCompanyHtmlMail(String to, String subject, String content) {
        sendCompanyMail(to, subject, content, 1);
    }

    public void sendCompanyMail(String to, String subject, String content) {
        sendCompanyMail(to, subject, content, 0);
    }

    /**
     * 企业邮箱发送，需要配置ssl
     * @Param [to, subject, content]
     * @return void
     */
    public void sendCompanyMail(String to, String subject, String content, int type) {
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", protocol);
        prop.setProperty("mail.smtp.host", host);
        prop.setProperty("mail.smtp.port", port);
        prop.setProperty("mail.smtp.auth", "true");
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);
        Session session = Session.getDefaultInstance(prop, new MyAuthenricator(account, pass));
        session.setDebug(true);
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(account, "fosuhub"));  // 自定义发送名
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(new Date());
            if (type == 1) {
                // html
                mimeMessage.setContent(content, "text/html;charset=utf-8");
            } else {
                // text
                mimeMessage.setText(content);
            }
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }
    }

    static class MyAuthenricator extends Authenticator {
        String u = null;
        String p = null;

        public MyAuthenricator(String u, String p) {
            this.u = u;
            this.p = p;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(u, p);
        }
    }
}


