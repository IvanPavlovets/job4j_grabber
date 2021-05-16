package ru.job4j;

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
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        Elements time = doc.select("td:nth-child(6)");
        System.out.println("Получено html элементов: " + row.size());
        for (int i = 0; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            System.out.println(time.get(i).text());
        }
    }

}
