package ru.job4j.html;

import ru.job4j.model.Post;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс извлечение данных с сайта.
 */
public interface Parse {
    /**
     * Метод загружает список обьектов,
     * всех постов - обьявлений.
     * @param link
     * @return
     */
    List<Post> list(String link);

    /**
     * Загружает детали одного поста - обьявления.
     * @param link
     * @return
     */
    Post detail(String link) throws IOException;
}
