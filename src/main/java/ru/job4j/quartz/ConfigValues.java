package ru.job4j.quartz;

import java.io.InputStream;
import java.util.Properties;

/**
 * Класс подключения к настройкам файла .properties.
 */

public class ConfigValues {
    private final Properties prop = new Properties();

    private final String fileName;

    public ConfigValues(String fileName) {
        this.fileName = fileName;
        this.loadPropValues();
    }

    /**
     * Метод считывает пары строк (key/value) из файла .properties в свою внутрюнию карту.
     */
    public void loadPropValues() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            prop.load(inputStream);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Метод достает искомый парметр из внутреней карты, по ключу.
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return this.prop.getProperty(key);
    }
}



