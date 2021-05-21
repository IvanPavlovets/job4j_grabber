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
    public static void main(String[] args) throws IOException {
        String resource = "https://www.sql.ru/forum/job-offers";
        for (int i = 1; i <= 5; i++) {
            Document doc = Jsoup.connect(resource + "/" + String.valueOf(i)).get();
            Elements row = doc.select(".postslisttopic");
            Elements time = doc.select("td:nth-child(6)");
            System.out.println("Получено html элементов: " + row.size());
            for (int q = 0; q < row.size(); q++) {
                Element href = row.get(q).child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(time.get(q).text());
            }
        }
    }

}
