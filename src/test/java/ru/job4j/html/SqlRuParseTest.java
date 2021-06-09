package ru.job4j.html;

import org.junit.Test;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SqlRuParseTest {

    @Test
    public void whenGetSelection() {
        String javas = "Вакансия Full stack JavaScript (NodeJS and ReactJS),  полная занятость , 1800-4500$ [new]";
        String java1 = "Вакансия Full stack Java (NodeJS and ReactJS),  полная занятость , 1800-4500$ [new]";
        String java2 = "Вакансия Full stack JavaScript Java (NodeJS and ReactJS),  полная занятость , 1800-4500$ [new]";
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        SqlRuParse parse = new SqlRuParse(timeParser);
        try {
            assertThat(parse.selection(java1), is(true));
            assertThat(parse.selection(java2), is(true));
            assertThat(parse.selection(javas), is(false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
