package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс перевода дат, вида:
 * 1) "11 май 2021, 14:25"
 * 2) "вчера, 12:14"
 * 3) "сегодня, 10:52"
 */
public class SqlRuDateTimeParser implements DateTimeParser {

    private final static Map<String, Integer> MONTH = new HashMap<>() {
        {
            put("янв", 1);
            put("фев", 2);
            put("мар", 3);
            put("апр", 4);
            put("май", 5);
            put("июн", 6);
            put("июл", 7);
            put("авг", 8);
            put("сен", 9);
            put("окт", 10);
            put("ноя", 11);
            put("дек", 12);
        }
    };

    /**
     * Метод преобразует шаблоную дату с типом String в обьект LocalDateTime.
     * Разбитые подстроки образуют обьекты LocalDate и LocalTime.
     * Строковому шаблону каждого месяца соответсвует int значение.
     * Строковые шаблоны "сегодня" и "вчера" - переводяться в часном порядке.
     * DateTimeFormatter - класс задания шаблона для LocalDate и LocalTime.
     * @param parse
     * @return обьект LocalDateTime
     */
    @Override
    public LocalDateTime parse(String parse) {
        String[] parts = parse.split(", ");
        DateTimeFormatter dTF2 = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime lt = LocalTime.parse(parts[1], dTF2);
        LocalDate ld;
        if (parts[0].equals("сегодня")) {
            ld = LocalDate.now();
        } else if (parts[0].equals("вчера")) {
            ld = LocalDate.now().minusDays(1);
        } else {
            String[] parts1 = parts[0].split(" ");
            ld = (LocalDate) LocalDate.of(
                    Integer.parseInt(parts1[2]),
                    MONTH.get(parts1[1]),
                    Integer.parseInt(parts1[0]));
        }
        return ld.atTime(lt);
    }
}
