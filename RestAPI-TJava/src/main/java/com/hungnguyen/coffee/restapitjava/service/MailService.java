package com.hungnguyen.coffee.restapitjava.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("{spring.mail.from}")
    private String emailFrom;

    public String sendEmail(String recipients, String subject, String body, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
      log.info("Sending mail...");
      MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        try {
            helper.setFrom(emailFrom, "Hung Java"); // gan ten
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if(recipients.contains(",")){
            helper.setTo(InternetAddress.parse(recipients)); //gui nhieu nguoi
        }else{
            helper.setTo(recipients);
        }

        //xu li file dinh kem
        if(files != null){
            for(MultipartFile file : files){
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        //set subject va content
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
        log.info("Mail sent!, recipients={}", recipients);

        return "sent";
    }

    public void sendConfirmLink(String email, Long userId, String secretCode) throws MessagingException,
            UnsupportedEncodingException {
        log.info("Sending email confirmation...");

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        Context context = new Context();
        String linkConfirm = String.format("http://localhost:8080/user/confirm/%s?sercretCode=%s", userId, secretCode);

        Map<String, Object> properties = new HashMap<>();

        properties.put("linkConfirm", linkConfirm);

        context.setVariables(properties);

        helper.setFrom(emailFrom, "ABC");
        helper.setTo(email);
        helper.setSubject("Confirmation your account");

        String html = springTemplateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Mail sent!, email={}", email);

    }

    @KafkaListener(topics = "confirm-account-topic", groupId = "confirm-account-group") //lắng nghe theo topic truyền
    // vào
    public void sendConfirmLinkByKafka(String message) throws MessagingException,
            UnsupportedEncodingException {
        log.info("Sending email confirmation... by kafka...");

        String[] arr = message.split(",");

        String emailTo = arr[0].substring(arr[0].indexOf('=') + 1);

        String userId = arr[1].substring(arr[1].indexOf('=') + 1);

        String verifyCode = arr[2].substring(arr[2].indexOf('=') + 1);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");

        Context context = new Context();
        String linkConfirm = String.format("http://localhost:8080/user/confirm/%s?sercretCode=%s", userId, verifyCode);

        Map<String, Object> properties = new HashMap<>();

        properties.put("linkConfirm", linkConfirm);

        context.setVariables(properties);

        helper.setFrom(emailFrom, "ABC");
        helper.setTo(emailTo);
        helper.setSubject("Confirmation your account");

        String html = springTemplateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("Mail sent!, email={}", emailTo);

    }
}
