package com.bit.eduventure.Email.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendHtmlMessage(String to, String subject, String text) throws jakarta.mail.MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        // HTML content, including an image and changing font color
        String htmlContent = "<h1 style='color:red;'>Welcome to Edu-Venture!</h1>" +
                "<p style='color:blue; font-size:20px'>" + text + "</p>" +
                "<img src='https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDnYnN%2FbtspeoPI9jp%2F1QCUSI2i2hX3PK4ibnDv60%2Fimg.png' />";

        helper.setText(htmlContent, true);

        // Attach an image with Content ID
//        ClassPathResource image = new ClassPathResource("path/to/image.jpg");
//        helper.addInline("image", image);

        emailSender.send(message);
    }
}
