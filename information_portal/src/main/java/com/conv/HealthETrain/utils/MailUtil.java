package com.conv.HealthETrain.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

/**
 * @author liusg
 */
@Component
@RequiredArgsConstructor
public class MailUtil {

    @Value("${spring.mail.from}")
    private String from;
    private final JavaMailSender mailSender;


    /**
     * 发送邮件
     * @param to
     * @param subject
     * @param content
     * @return
     */
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}
