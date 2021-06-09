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

    private static final String RESOURCE = "https://www.sql.ru/forum/job-offers";
    private static final String JAVA = "java";
    private static final String JAVAS = "javas";

    private DateTimeParser timeParser;

    public SqlRuParse(DateTimeParser parser) {
        timeParser = parser;
    }

    /**
     * Метод записывает в список обьекты обьявлений,
     * по критерию selection().
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
            for (Element row : posts) {
                Element href = row.child(0);
                link = href.attr("href");
                Post post = this.detail(link);
                if (!(selection(post.getName())) || !(selection(post.getTextDescription()))) {
                    continue;
                }
                postList.add(post);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return postList;
    }

    /**
     * Внутрений метод проверки строки по критерию, необходим для
     * выбор тех обьектов в описании или заглавии которых
     * содержиться искомое совпадение слов "Java".
     *
     * @param testStr
     * @return boolean по результату проверки на "Java"
     * @throws IOException
     */
    public boolean selection(String testStr) throws IOException {
        boolean result = false;
        //String name = "Вакансия Full stack (NodeJS and ReactJS), javaScript  полная занятость , 1800-4500$ [new]";//post.getName().toLowerCase();
        testStr = testStr.toLowerCase();
        if (testStr.contains(JAVAS)) {
            testStr = testStr.replaceAll(JAVAS, "");
            if (testStr.contains(JAVA)) {
                result = true;
            }
        } else if (testStr.contains(JAVA)) {
            result = true;
        }
        return result;
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
        return new Post(msgHeader, link, msgText, timeParser.parse(date));
    }

    /**
     * Метод получает список из нескольких страниц интеренет ресурса.
     *
     * @return List<String>
     */
    public List<String> pages() {
        List<String> result = new ArrayList<>();
        String page;
        for (int i = 1; i <= 5; i++) { // крупные страницы
            page = RESOURCE + "/" + String.valueOf(i);
            result.add(page);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        SqlRuDateTimeParser timeParser = new SqlRuDateTimeParser();
//
        SqlRuParse parse = new SqlRuParse(timeParser);
//        for (int i = 1; i <= 5; i++) { // крупные страницы
//            String page = RESOURCE + "/" + String.valueOf(i);
//            parse.list(page);
//        }
        //System.out.println(parse.selection());


    }

}
