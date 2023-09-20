package org.finalproject.tmeroom.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async("mailAsync")
    public void sendEmail(String emailAddress, String subject, String content, boolean isHtml, boolean isMultipart) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isMultipart);

            helper.setTo(emailAddress);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            log.error("Exception thrown during email creation, message={}", e.getMessage());
        }
    }
}
