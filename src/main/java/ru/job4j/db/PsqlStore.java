package ru.job4j.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.Post;
import ru.job4j.utils.ConfigValues;

import java.sql.*;
import java.time.LocalDateTime;
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
        this.cn = initConnection();
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

    /**
     * Метод сохранения одного поста.
     * В цикле метод next() проходит по записям из БД.
     * v 1.2
     * запись - ON CONFLICT ON CONSTRAINT - игнорирует дубликаты в бд.
     * @param post
     */
    @Override
    public void save(Post post) {
        String insertQueryStatement =
                "INSERT INTO post(name, link, description, created) VALUES (?,?,?,?)"
                        + "ON CONFLICT ON CONSTRAINT post_link_key DO NOTHING";
        try (PreparedStatement ps =
                     cn.prepareStatement(insertQueryStatement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getTextDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getDateCreation()));
            ps.executeUpdate();
            LOG.debug("{} успешно добавлена", post.getName());
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getString(1));
                    LOG.debug("Сгенерированый Id: {}", post.getId());
                } else {
                    LOG.debug("Вставка данных не удалась");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * В методе реализован принцеп пакетной обработки для записи
     * вакансий в бд - все вакансии подгружаються в set в памяти
     * и после это происход запись в бд полным пакетом, единожды.
     * @param posts
     */
    @Override
    public void saveAll(List<Post> posts) {
        String insertQueryStatement =
                "INSERT INTO post(name, link, description, created) VALUES (?,?,?,?)"
                        + "ON CONFLICT ON CONSTRAINT post_link_key DO NOTHING";
        try {
            cn.setAutoCommit(false);
            try (PreparedStatement ps
                         = cn.prepareStatement(insertQueryStatement)) {
                LOG.debug("Save {} items", posts.size());
                for (Post post : posts) {
                    LOG.debug("Saving post with name: {}", post.getName());
                    ps.setString(1, post.getName());
                    ps.setString(2, post.getLink());
                    ps.setString(3, post.getTextDescription());
                    ps.setTimestamp(4, Timestamp.valueOf(post.getDateCreation()));
                    ps.addBatch();
                }
                LOG.debug("Query executed");
                ps.executeBatch();
            }
            cn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.debug("Saving completed");
    }


    /**
     * Метода достает самую "свежею дату"
     * последений добавленой вакансии с sql.ru.
     * Если в бд нет вакансий то
     * самая свежая дата time -
     * 6 месяцев назад от настоящего
     * @return LocalDateTime
     */
    @Override
    public LocalDateTime getLastDate() {
        String query = "select max(created) from post";
        LOG.debug("Достаем последнюю дату");
        LocalDateTime time = null;
        try (Statement st = cn.createStatement()) {
            try (ResultSet rs = st.executeQuery(query)) {
                if (rs.next()) {
                    Timestamp rawDbTime = rs.getTimestamp(1);
                    if (rawDbTime == null) {
                        time = LocalDateTime.parse("0021-01-12T16:46");
                    } else {
                        LocalDateTime dbTime = rawDbTime.toLocalDateTime();
                        time = dbTime;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.debug("Полученая дата: {}", time);
        return time;
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
            LOG.debug("Выборка всех обьявлений завершена, кол-во: {}", result.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * метод возвращает обьявление по id.
     * @param id
     * @return обьект Post
     */
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
