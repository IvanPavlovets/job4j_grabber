package ru.job4j.html;

import java.time.LocalDateTime;

/**
 * Класс объявление на сайте sql.ru
 */
public class Post {
    /**
     * Первичный ключ.
     */
    private String id;

    /**
     * текст ссылки обьявления (поста).
     */
    private String name;

    /**
     * url, ссылка поста.
     */
    private String link;

    /**
     * Описание вакансии
     */
    private String textDescription;

    /**
     * дата создания поста
     */
    private LocalDateTime dateCreation;

    public Post() {
    }

    public Post(String name, String link, String textDescription, LocalDateTime dateCreation) {
        this.name = name;
        this.link = link;
        this.textDescription = textDescription;
        this.dateCreation = dateCreation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
