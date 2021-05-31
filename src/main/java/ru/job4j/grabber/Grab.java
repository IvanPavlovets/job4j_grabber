package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.job4j.db.Store;
import ru.job4j.html.Parse;

/**
 * Интерфейс использует библиотеку quartz - планировщик заданий,
 * для запуска парсера сайта. Парсинг с помощью библиотеки Json
 */
public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
