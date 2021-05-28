package ru.job4j.html;

import java.io.IOException;
import java.util.List;

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
