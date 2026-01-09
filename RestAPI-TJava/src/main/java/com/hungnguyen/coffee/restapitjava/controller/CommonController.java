package com.hungnguyen.coffee.restapitjava.controller;

import com.hungnguyen.coffee.restapitjava.dto.response.ResponseData;
import com.hungnguyen.coffee.restapitjava.dto.response.ResponseError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
@Slf4j
@RequiredArgsConstructor
public class CommonController {
    private final MailSender mailSender;

    @PostMapping("/send-email")
    public ResponseData<String> sendEmail(@RequestParam String recipients, @RequestParam String subject, @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        try {
            return ResponseData<>(HttpStatus.ACCEPTED, mailSender.sendEmail(recipients, subject, content, files));
        }catch (Exception e){
            log.error("Send email failed!", e);
            return new ResponseError(HttpStatus.BAD_REQUEST,"Send failure");
        }
    }
}
