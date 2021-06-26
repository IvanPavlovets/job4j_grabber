package ru.job4j.html;

import ru.job4j.model.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * Интерфейс извлечение данных с сайта.
 */
public interface Parse {
    /**
     * Метод загружает список обьектов,
     * всех постов - обьявлений.
     * @param link
     * @param until
     * @return список всех постов на 5 страницах  List<Post>.
     */
    List<Post> list(String link, LocalDateTime until);

    /**
     * Загружает детали одного поста - обьявления.
     * @param link
     * @return Post
     */
    Post detail(String link) throws IOException;

    /**
     * Загружает список страниц ресурса для парсинга.
     * @return List<String>
     */
    List<String> pages();
}
