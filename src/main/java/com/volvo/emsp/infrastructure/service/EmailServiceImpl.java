package com.volvo.emsp.infrastructure.service;

import com.volvo.emsp.domain.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String text) {
        log.info("Sending email to: {}", to);
        log.info("Subject: {}", subject);
        log.info("Text: {}", text);
    }
}
