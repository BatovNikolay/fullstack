package ru.nbatov.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.nbatov.model.Notification;
import ru.nbatov.service.IMailService;

import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(value = "mail.realisation.spring")
public class MailService implements IMailService {

    private final JavaMailSender javaMailSender;
    private final VanillaMailService mailService;

    @Autowired
    public MailService(JavaMailSender javaMailSender, Environment env) {
        this.javaMailSender = javaMailSender;
        mailService = new VanillaMailService(env);
    }

    @Override
    public boolean sendMail(Notification notification) {
        boolean result = true;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getTo());
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());
        try {
            this.javaMailSender.send(message);
        } catch (MailException exc) {
            log.error("Error send mail to: " + notification.getTo(), exc);
            result = false;
        }
        return result;
    }

    @Override
    public List<Notification> getMails() {
        return mailService.getMails();
    }
}
