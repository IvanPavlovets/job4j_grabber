package ru.job4j.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Post;
import ru.job4j.utils.ConfigValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PsqlStore implements Store, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    /**
     * Соединение с бд, написание sql запросов
     */
    private Connection cn;

    private ConfigValues config;

    /**
     * В конструкторе принимаем утилиту доступа к ресурсам.
     *
     * @param config читает значения файлов .properties
     */
    public PsqlStore(ConfigValues config) {
        this.config = config;
        initConnection();
    }

    public PsqlStore(Connection cn) {
        this.cn = cn;
    }

    private Connection initConnection() {
        Connection cn = null;
        String url = config.get("rabbit.url");
        String login = config.get("rabbit.username");
        String password = config.get("rabbit.password");
        try {
            Class.forName(config.get("rabbit.driver-class-name"));
            cn = DriverManager.getConnection(url, login, password);
            if (cn != null) {
                System.out.println("Соединение успешно созданно! Можно добавлять данные");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cn;
    }

    @Override
    public void save(Post post) {
        String insertQueryStatement =
                "INSERT INTO post(name, link, description, created) VALUES (?,?,?,?)";
        try (PreparedStatement ps =
                     cn.prepareStatement(insertQueryStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getTextDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getDateCreation()));
            ps.executeUpdate();
            LOG.debug(post.getName() + " успешно добавлена");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getString(1));
                    LOG.debug("Сгенерированый Id: " + post.getId());
                } else {
                    LOG.debug("Вставка данных не удалась");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        String query = "Select * FROM post";
        try (PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Post post = new Post(
                        rs.getString("name"),
                        rs.getString("link"),
                        rs.getString("description"),
                        rs.getTimestamp("created").toLocalDateTime()
                );
                post.setId(rs.getString("id"));
                result.add(post);
            }
            LOG.debug("Выборка всех обьявлений завершена, кол-во: " + result.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post findById(String id) {
        Post result = null;
        String query = "Select * from post p WHERE p.id = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result = new Post(
                            rs.getString("name"),
                            rs.getString("link"),
                            rs.getString("description"),
                            rs.getTimestamp("created").toLocalDateTime()
                    );
                    result.setId(rs.getString("id"));
                }
                LOG.debug("Поиск, по имени обьявления, завершен");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }
}
