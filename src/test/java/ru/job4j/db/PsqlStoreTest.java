package ru.job4j.db;

import org.junit.Test;
import ru.job4j.model.Post;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PsqlStoreTest {
    /**
     * Метод создает соединение для нужд тестов.
     * @return Connection
     */
    public Connection init() {
        try (InputStream in = PsqlStore.class.getClassLoader().
                getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("rabbit.driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("rabbit.url"),
                    config.getProperty("rabbit.username"),
                    config.getProperty("rabbit.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Тест проверяет создание обьекта post с применением
     * метода откатывания изменений в обьекте Connection.
     * тоесть обьект post создаеться в виртуальной БД (в памяти)
     * и потом вызываеться метод rollback()
     * и состояние откатываеться назад до вызова метода createItem().
     *
     * @throws Exception
     */
    @Test
    public void createItem() throws Exception {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post post1 = new Post("javaPost", "link", "desc", LocalDateTime.now());
            store.save(post1);
            Post post2 = store.findById(post1.getId());
            assertThat(store.findById(post1.getId()).getName(), is("javaPost"));
            assertEquals(post1.getName(), post2.getName());
            assertEquals(post1.getLink(), post2.getLink());
            assertEquals(post1.getTextDescription(), post2.getTextDescription());
            assertNotNull(post2.getTextDescription());
            assertNotNull(post2.getId());
        }
    }

    /**
     * Тест проверяет нахождение обьекта post по id.
     * В тесте идет сравнение двух обьектов post1 (создан в виртуальной БД)
     * и post2 (созданого вне БД)
     * @throws Exception
     */
    @Test
    public void whenFindById() throws Exception {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post post1 = new Post("javaPost", "link", "desc", LocalDateTime.now());
            store.save(post1);
            Post post2 = store.findById(post1.getId());
            assertEquals(post1.getName(), post2.getName());
            assertEquals(post1.getLink(), post2.getLink());
            assertEquals(post1.getTextDescription(), post2.getTextDescription());
            assertNotNull(post2.getTextDescription());
            assertNotNull(post2.getId());
        }
    }
}
