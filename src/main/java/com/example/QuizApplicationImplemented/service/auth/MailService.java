package com.example.QuizApplicationImplemented.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class MailService {

//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendEmail(String to , String subject , String body){
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//
//        try {
//            mailSender.send(message);
//            System.out.println("Email sent successfully to :" + to);
//        }catch (Exception ex){
//            System.out.println("Error sending email" + ex.getMessage());
//        }
//
//    }
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("{spring.mail.username}")
    private String fromEmail;

    @Async
    public CompletableFuture<Boolean> sendEmail(String to , String subject , String body){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setSubject(subject);
            message.setText(to);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email sent successfully to : {}" , to);
            return CompletableFuture.completedFuture(true);
        }catch (Exception ex){
            logger.error("Error sending email to {} : {}" , to , ex.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    public CompletableFuture<Boolean> sendHtmlEmail(String to , String subject , String htmlBody){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody , true);

            mailSender.send(message);
            logger.info("HTML email sent successfully to  : {}" , to);
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            logger.info("Error sending HTML email to {} : {}" , to , e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    public void sendWelcomeEmail(String to , String name){
        String subject = "Welcome to Quiz Application";

        String body = String.format("""
                            Hello %s,
                           \s
                            Welcome to Quiz Application! Your account has been successfully created.
                           \s
                            You can now log in and start creating or taking quizzes.
                           \s
                            If you have any questions, feel free to reach out to our support team.
                           \s
                            Best regards,
                            Quiz Application Team
                """ , name != null ? name : "User");

        sendEmail(to , subject , body);
    }

    public void sendPasswordChangeNotification(String to , String name){
        String subject = "Password Changed Successfully";
        String body = String.format("""
                            Hello %s,
                           \s
                            Your password has been successfully changed.
                           \s
                            If you did not make this change, please contact our support team immediately.
                           \s
                            Best regards,
                            Quiz Application Team
                """ , name != null ? name : "User");

        sendEmail(to , subject , body);
    }


}
