package ru.nbatov.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    ERROR_CLOSE(""),
    INTERNAL_ERROR(""),
    ERROR_GET_MAILS(""),
    ERROR_READ_MAIL("Ошибка чтения письма");
    private String msg;
}
