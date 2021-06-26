package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Post;
import ru.job4j.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class.getName());

    private DateTimeParser timeParser;

    public SqlRuParse(DateTimeParser parser) {
        timeParser = parser;
    }

    /**
     * Метод записывает в список обьекты обьявлений,
     * по критерию selection().
     * v 1.2
     * вакансии парсяться церез обратный цикл,
     * начиная от самой "старой", за посление 6 месяцев.
     * Url страниц тоже  подаеться в обратном порядке.
     *
     * @param link url одной страницы - https://www.sql.ru/forum/job-offers/1
     * @param
     * @return список всех постов на 5 страницах.
     */
    @Override
    public List<Post> list(String link, LocalDateTime lastDate) {
        List<Post> postList = new ArrayList<>();
        postList.clear();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Elements posts = doc.select(".postslisttopic");
            for (int i = posts.size() - 1; i >= 0; i--) {
                Element href = posts.get(i).child(0);
                link = href.attr("href");
                doc = Jsoup.connect(link).get();
                Elements msg = doc.select(".msgTable");
                String msgText = msg.first().select(".msgBody").get(1).text();
                String msgHeader = msg.first().select(".messageHeader").text();
                if (!(selection(msgHeader)) || !(selection(msgText))) {
                    continue;
                }
                String rawDate = msg.first().select(".msgFooter").text();
                rawDate = rawDate.substring(0, rawDate.indexOf(" ["));
                LocalDateTime date = timeParser.parse(rawDate);
                if (date.isAfter(lastDate)) {
                    LOG.debug("{} - Дата подходит!", date);
                    Post post = new Post(msgHeader, link, msgText, date);
                    postList.add(post);
                }
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
     * v 1.2
     * Url страниц подаються в обратном порядке,
     * что бы чтение начиналось с самой последней вакансии.
     *
     * @return List<String>
     */
    public List<String> pages() {
        List<String> result = new ArrayList<>();
        result.clear();
        String page;
        for (int i = 5; i >= 1; i--) { // крупные страницы
            page = RESOURCE + "/" + String.valueOf(i);
            result.add(page);
        }
        return result;
    }
}
