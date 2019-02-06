package ru.nbatov.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nbatov.model.Notification;
import ru.nbatov.service.IMailService;

import java.util.List;

@RestController
@RequestMapping("/mail")
public class EMailController {

    private final IMailService mailService;

    public EMailController(IMailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Notification>> getMails() {
        return ResponseEntity.ok(mailService.getMails());
    }

    @PostMapping("/send")
    public ResponseEntity<Boolean> sendMail(@RequestBody Notification notification) {
        return ResponseEntity.ok(mailService.sendMail(notification));
    }
}
