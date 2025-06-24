package com.volvo.emsp.domain.service;

public interface EmailService {

    void sendEmail(String to, String subject, String text);
}