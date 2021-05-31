package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.model.Post;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс парсинга сайта по URL.
 * Document - принимает всю web страницу целиком из которой
 * вытаскиваем отделные элементы.
 * Elements - хранилище выбраных элементов web страницы,
 * выбираються по кретерию. Кретерий смотри в html- коде страницы.
 * Element - определеный единичный элемент.
 * Element href = td.child(0) - первый элемент.
 */
public class SqlRuParse implements Parse {

    private DateTimeParser timeParser;

    public SqlRuParse(DateTimeParser parser) {
        timeParser = parser;
    }

    /**
     * Метод загружает список обьектов,
     * всех постов - обьявлений.
     *
     * @param link url одной страницы - https://www.sql.ru/forum/job-offers/1
     * @return список всех постов на 5 страницах.
     */
    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Elements posts = doc.select(".postslisttopic");
            System.out.println("Получено строк с обьявлениями: " + posts.size());
            for (Element row : posts) {
                Element href = row.child(0);
                link = href.attr("href");
                postList.add(this.detail(link));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return postList;
    }

    /**
     * Метод возвращает детали поста (обьявления) по переданой ссылке.
     * msg - основная таблица обьявления содержит - описание, заголовок, дату.
     * msgText - текст описания.
     * msgHeader - заголовок.
     *
     * @param link - переданая ссылка
     * @throws IOException
     */
    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Elements msg = doc.select(".msgTable");
        String msgText = msg.first().select(".msgBody").get(1).text();
        String msgHeader = msg.first().select(".messageHeader").text();
        String date = msg.last().select(".msgFooter").text();
        date = date.substring(0, date.indexOf(" ["));
        System.out.println("1 link: " + link);
        System.out.println("2 msgHeader: " + msgHeader);
        System.out.println("3 date: " + date);
        return new Post(msgHeader, link, msgText, timeParser.parse(date));
    }

    public static void main(String[] args) throws IOException {
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
        String resource = "https://www.sql.ru/forum/job-offers";
        SqlRuParse parse = new SqlRuParse(timeParser);
        String link;
        Map<String, Post> postMap = new HashMap<>();
        for (int i = 1; i <= 1; i++) { // крупные страницы
            String page = resource + "/" + String.valueOf(i);
            parse.list(page);
        }
    }

}
