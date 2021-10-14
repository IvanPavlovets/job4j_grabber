package ru.job4j.db;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.model.Post;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PsqlStoreTest {

    private static Connection connection;

    /**
     * Метод создает соединение для нужд тестов.
     *
     * @return Connection
     */
    @BeforeClass
    public static void init() {
        try (InputStream in = PsqlStoreTest.class.getClassLoader().
                getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @After
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from post")) {
            statement.execute();
        }
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * Тест проверяет создание обьекта post с применением
     * метода откатывания изменений в обьекте Connection.
     * тоесть обьект post создаеться в виртуальной БД (в памяти)
     * и потом вызываеться метод rollback()
     * и состояние откатываеться назад до вызова метода createItem().
     *
     * @Test
     *     public void createItem() throws Exception {
     *         PsqlStore store = new PsqlStore(connection);
     *         Post post1 = new Post("javaPost", "link", "desc", LocalDateTime.now());
     *         store.save(post1);
     *         Post post2 = store.findById(post1.getId());
     *         assertThat(store.findById(post1.getId()).getName(), is("javaPost"));
     *         assertEquals(post1.getName(), post2.getName());
     *         assertEquals(post1.getLink(), post2.getLink());
     *         assertEquals(post1.getTextDescription(), post2.getTextDescription());
     *         assertNotNull(post2.getTextDescription());
     *         assertNotNull(post2.getId());
     *     }
     *
     * @throws Exception
     */


    /**
     * Тест проверяет нахождение обьекта post по id.
     * В тесте идет сравнение двух обьектов post1 (создан в виртуальной БД)
     * и post2 (созданого вне БД)
     *
     *  @Test
     *     public void whenFindById() throws Exception {
     *         PsqlStore store = new PsqlStore(connection);
     *         Post post1 = new Post("javaPost", "link", "desc", LocalDateTime.now());
     *         store.save(post1);
     *         Post post2 = store.findById(post1.getId());
     *         assertEquals(post1.getName(), post2.getName());
     *         assertEquals(post1.getLink(), post2.getLink());
     *         assertEquals(post1.getTextDescription(), post2.getTextDescription());
     *         assertNotNull(post2.getTextDescription());
     *         assertNotNull(post2.getId());
     *     }
     *
     * @throws Exception
     */
}
