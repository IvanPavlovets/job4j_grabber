package ru.job4j.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class SqlRuDateTimeParserTest {
    @Test
    public void whenInputToday() {
        LocalDateTime now = LocalDateTime.now();
        String input = String.format("сегодня, %d:%d", now.getHour(), now.getMinute());
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        LocalDateTime out = timeParser.parse(input);
        Assert.assertEquals(now.getDayOfMonth(), out.getDayOfMonth());
        Assert.assertEquals(now.getMonthValue(), out.getMonthValue());
        Assert.assertEquals(now.getYear(), out.getYear());
        Assert.assertEquals(now.getHour(), out.getHour());
        Assert.assertEquals(now.getMinute(), out.getMinute());
    }

    @Test
    public void whenInputYesterday() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        String input = String.format("вчера, %d:%d", yesterday.getHour(), yesterday.getMinute());
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        LocalDateTime out = timeParser.parse(input);
        Assert.assertEquals(yesterday.getDayOfMonth(), out.getDayOfMonth());
        Assert.assertEquals(yesterday.getMonthValue(), out.getMonthValue());
        Assert.assertEquals(yesterday.getYear(), out.getYear());
        Assert.assertEquals(yesterday.getHour(), out.getHour());
        Assert.assertEquals(yesterday.getMinute(), out.getMinute());
    }

    @Test
    public void whenInputNormalDate() {
        LocalDateTime expect = LocalDateTime.of(21, 5, 20, 11, 12);
        String input = String.format("20 май 21, %d:%d", expect.getHour(), expect.getMinute());
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        LocalDateTime out = timeParser.parse(input);
        Assert.assertEquals(expect.getDayOfMonth(), out.getDayOfMonth());
        Assert.assertEquals(expect.getMonthValue(), out.getMonthValue());
        Assert.assertEquals(expect.getYear(), out.getYear());
        Assert.assertEquals(expect.getHour(), out.getHour());
        Assert.assertEquals(expect.getMinute(), out.getMinute());
    }
}
