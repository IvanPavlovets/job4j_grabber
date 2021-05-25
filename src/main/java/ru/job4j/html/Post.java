package ru.job4j.html;

import java.sql.Timestamp;

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
    private Timestamp dateCreation;

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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
