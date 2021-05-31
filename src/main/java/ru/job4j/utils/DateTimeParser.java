package ru.job4j.utils;

import java.time.LocalDateTime;

/**
 * Интерфейс преобразования даты времени.
 */
public interface DateTimeParser {
    LocalDateTime parse(String parse);
}
