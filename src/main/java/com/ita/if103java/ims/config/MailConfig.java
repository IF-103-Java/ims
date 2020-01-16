package com.ita.if103java.ims.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class MailConfig {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.port}")
    private String port;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.debug}")
    private String debug;

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.smtp.auth}")
    private String authentication;

    @Value("${mail.smtp.starttls.enable}")
    private String startTLS;


    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", protocol);
        properties.put("mail.smtp.auth", authentication);
        properties.put("mail.smtp.starttls.enable", startTLS);
        properties.put("mail.debug", debug);
        properties.put("mail.smtp.ssl.trust", host);

        return mailSender;
    }
}
