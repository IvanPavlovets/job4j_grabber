package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Класс парсинга сайта по URL.
 * Document - принимает всю web страницу целиком из которой
 * вытаскиваем отделные элементы.
 * Elements - хранилище выбраных элементов web страницы,
 * выбираються по кретерию. Кретерий смотри в html- коде страницы.
 * Element - определеный единичный элемент.
 * Element href = td.child(0) - первый элемент.
 */
public class SqlRuParse {

    /**
     * Метод возвращает детали поста (обьявления) по переданой ссылке.
     * msg - основная таблица обьявления содержит - описание, заголовок, дату.
     * msgText - текст описания.
     * msgHeader - заголовок.
     *
     * @param urlLink - переданая ссылка
     * @throws IOException
     */
    public void detail(String urlLink) throws IOException {
        Document doc = Jsoup.connect(urlLink).get();
        Elements msg = doc.select(".msgTable");
        String msgText = msg.first().select(".msgBody").get(1).text();
        String msgHeader = msg.first().select(".messageHeader").text();
        String date = msg.last().select(".msgFooter").text();
        date = date.substring(0, date.indexOf(" ["));
        System.out.println("1) urlLink: " + urlLink);
        System.out.println("2) msgText: " + msgText);
        System.out.println("3) msgHeader: " + msgHeader);
        System.out.println("4) date: " + date);
    }

    public static void main(String[] args) throws IOException {
        String resource = "https://www.sql.ru/forum/job-offers";
        SqlRuParse parse = new SqlRuParse();
        String link;
        for (int i = 1; i <= 5; i++) {
            Document doc = Jsoup.connect(resource + "/" + String.valueOf(i)).get();
            Elements rows = doc.select(".postslisttopic");
            System.out.println("Получено html элементов: " + rows.size());
            for (Element row : rows) {
                Element href = row.child(0);
                link = href.attr("href");
                parse.detail(link);
            }
        }
    }

}
