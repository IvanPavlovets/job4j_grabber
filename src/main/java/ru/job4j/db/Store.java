package ru.job4j.db;

import ru.job4j.model.Post;

import java.time.LocalDateTime;
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
     * Метод сохраняет список вакансий в бд,
     * с помощью пакетной обработки.
     * @param posts
     */
    void saveAll(List<Post> posts);

    /**
     * Метода достает самую "свежею дату"
     * последений добавленой вакансии с sql.ru.
     * @return LocalDateTime
     */
    LocalDateTime getLastDate();

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
