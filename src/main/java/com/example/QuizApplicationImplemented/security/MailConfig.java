package com.example.QuizApplicationImplemented.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

//    @Bean
//    public JavaMailSender getJavaMailSender() {
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername("blackplaindot@gmail.com");
//
//        Properties properties = mailSender.getJavaMailProperties();
//        properties.put("mail.transport.protocol" , "smtp");
//        properties.put("mail.smtp.auth" , "true");
//        properties.put("mail.smtp.starttls.enable" , "true");
//        properties.put("mail.debug" , "true");
//
//        return mailSender;
//    }

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);


        Properties prop = mailSender.getJavaMailProperties();
        prop.put("mail.transport.protocol" , "smtp");
        prop.put("mail.smtp.auth" , "true");
        prop.put("mail.smtp.starttls.enable" , "true");
        prop.put("mail.smtp.starttls.required" , "true");
        prop.put("mail.debug" , "false");
        prop.put("mail.smtp.ssl.trust" , mailHost);
        prop.put("mail.smtp.connectiontimeout" , "5000");
        prop.put("mail.smtp.timeout" , "5000");
        prop.put("mail.smtp.writetimeout" , "5000");

        return mailSender;
    }
}
