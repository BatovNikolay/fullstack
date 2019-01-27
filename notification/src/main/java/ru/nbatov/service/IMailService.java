package ru.nbatov.service;

import ru.nbatov.model.Notification;

import java.util.List;

public interface IMailService {
    boolean sendMail(Notification notification);
    List<Notification> getMails();
}
