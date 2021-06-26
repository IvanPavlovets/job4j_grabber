package ru.job4j.utils;

import java.time.LocalDateTime;

/**
 * Интерфейс преобразования даты времени.
 */
public interface DateTimeParser {
    /**
     * Метод преобразует шаблоную дату с типом String в обьект LocalDateTime.
     * @param parse String
     * @return LocalDateTime
     */
    LocalDateTime parse(String parse);
}
