package ru.nbatov.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.nbatov.model.Error;
import ru.nbatov.model.ErrorMessage;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
@ConditionalOnProperty(value = "mail.realisation.vanilla")
public class VanillaMailService implements IMailService {

    private static final String SERVICE_NAME = "Vanilla mail service bean ";

    private final Environment environment;

    @Getter
    private static final Properties propSmtp = new Properties();

    @Getter
    private static final Properties propPop3 = new Properties();

    @Autowired
    public VanillaMailService(Environment environment) {
        this.environment = environment;
    }

    private void initProperties() {
        if (propSmtp.isEmpty()) {
            propSmtp.put("mail.smtp.host", environment.getProperty("mail.smtp.host"));
            propSmtp.put("mail.smtp.socketFactory.port", environment.getProperty("mail.smtp.port"));
            propSmtp.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            propSmtp.put("mail.smtp.auth", "true");
            propSmtp.put("mail.smtp.port", environment.getProperty("mail.smtp.port"));
        }
        if (propPop3.isEmpty()) {
            propPop3.put("mail.pop3.host", environment.getProperty("mail.pop3.host"));
            propPop3.put("mail.pop3.port", environment.getProperty("mail.pop3.port"));
            propPop3.put("mail.pop3.starttls.enable", "true");
        }
    }

    @Override
    public boolean sendMail(Notification notification) {
        this.initProperties();
        boolean result = false;
        Session session = Session.getDefaultInstance(
                propSmtp,
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
            result = true;
        } catch (MessagingException e) {
            log.error(SERVICE_NAME + "- Error send mail", e);
        }
        return result;
    }

    @Override
    public List<Notification> getMails() {
        this.initProperties();
        Session emailSession = Session.getDefaultInstance(propPop3);
        List<Notification> result = new ArrayList<>();
        Folder inbox = null;
        Store store = null;
        try {
            store = emailSession.getStore("pop3s");
            store.connect(
                    environment.getProperty("mail.pop3.host"),
                    environment.getProperty("mail.auth.login"),
                    environment.getProperty("mail.auth.password")
            );
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Arrays.stream(inbox.getMessages()).forEach(next -> {
                Notification.NotificationBuilder builder = Notification.builder();
                try {
                    builder.from(next.getFrom()[0].toString());
                    builder.subject(next.getSubject());
                    builder.body(next.getContent().toString());
                } catch (MessagingException | IOException e) {
                    log.error(SERVICE_NAME + " - Error when read mail", e);
                    builder.error(Error.builder()
                            .errorMessage(ErrorMessage.ERROR_READ_MAIL)
                            .message(ErrorMessage.ERROR_READ_MAIL.getMsg())
                            .build()
                    );
                }
                result.add(builder.build());
            });
        } catch (MessagingException e) {
            log.error(SERVICE_NAME + " Get mails error", e);
            result.add(Notification.builder()
                    .error(Error.builder()
                            .errorMessage(ErrorMessage.INTERNAL_ERROR)
                            .message(ErrorMessage.INTERNAL_ERROR.getMsg())
                            .build())
                    .build());

        } finally {
            try {
                if (inbox != null) {
                    inbox.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                log.error(SERVICE_NAME + " - Error when try close", e);
                result.add(Notification.builder()
                        .error(Error.builder()
                                .errorMessage(ErrorMessage.ERROR_CLOSE)
                                .message(ErrorMessage.ERROR_CLOSE.getMsg())
                                .build())
                        .build());
            }
        }
        return result;
    }
}
