package ru.nbatov.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.nbatov.model.Notification;
import ru.nbatov.service.IMailService;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "mail.realisation.vanilla")
public class VanillaMailService implements IMailService {

    private final Environment environment;

    public VanillaMailService(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean sendMail(Notification notification) {
        Properties props = new Properties();
        props.put("mail.smtp.host", environment.getProperty("mail.smtp.host"));
        props.put("mail.smtp.socketFactory.port", environment.getProperty("mail.smtp.port"));
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", environment.getProperty("mail.smtp.port"));
        Session session = Session.getDefaultInstance(
                props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                environment.getProperty("mail.auth.login"),
                                environment.getProperty("mail.auth.password")
                        );
                    }
                }
        );
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nbatov722j@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(notification.getTo())
            );
            message.setSubject(notification.getSubject());
            message.setText(notification.getBody());
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Notification> getMails() {
        List<Notification> result = null;
        try {
            Properties properties = new Properties();
            properties.put("mail.pop3.host", environment.getProperty("mail.pop3.host"));
            properties.put("mail.pop3.port", environment.getProperty("mail.pop3.port"));
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("pop3s");
            store.connect(
                    environment.getProperty("mail.pop3.host"),
                    environment.getProperty("mail.auth.login"),
                    environment.getProperty("mail.auth.password")
            );
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            result = Arrays.stream(inbox.getMessages()).map(next -> {
                Notification.NotificationBuilder builder = Notification.builder();
                try {
                    builder.from(next.getFrom()[0].toString());
                    builder.subject(next.getSubject());
                    builder.body(next.getContent().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return builder.build();
            }).collect(Collectors.toList());
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
