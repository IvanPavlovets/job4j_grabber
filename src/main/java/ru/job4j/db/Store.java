package ru.job4j.db;

import ru.job4j.model.Post;

import java.util.List;

/**
 * Интерфейс связи с БД.
 */
public interface Store {
    /**
     * Метод сохраняет обьвление в базе данных.
     * @param post
     */
    void save(Post post);

    /**
     * Метод возвращает список всех обьявлений из БД.
     * @return
     */
    List<Post> getAll();

    /**
     * Метод возвращает одно обьяалений из БД по id.
     * @param id
     * @return
     */
    Post findById(String id);
}
