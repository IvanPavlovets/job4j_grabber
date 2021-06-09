package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.db.PsqlStore;
import ru.job4j.db.Store;
import ru.job4j.html.Parse;
import ru.job4j.html.SqlRuParse;
import ru.job4j.model.Post;
import ru.job4j.utils.ConfigValues;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    /**
     * утилита доступа к .properties
     */
    private ConfigValues cfg;

    /**
     * утилита преобразования даты времени.
     */
    private DateTimeParser sqlRuTimeParser;

    /**
     * метод получения PsqlStore,
     * обработчик связи с БД.
     *
     * @return PsqlStore
     */
    public Store store() {
        return new PsqlStore(cfg);
    }

    /**
     * метод получения SqlRuParse,
     * парсер html кода обьявлений sql.ru.
     *
     * @return SqlRuParse
     */
    public Parse parse() {
        return new SqlRuParse(sqlRuTimeParser);
    }

    /**
     * В методе получаем экземпляр планировщика, запускаем его и передаем.
     * В Scheduler добавляеться все задачи (JobDetail)
     * для переодического запуска.
     *
     * @return Scheduler
     * @throws SchedulerException
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Метод создает экземпляры утилитных классов:
     * 1) подключения к значениям файла .properties,
     * 2) обработчика даты - времени.
     */
    public void cfg() {
        cfg = new ConfigValues("rabbit.properties");
        sqlRuTimeParser = new SqlRuDateTimeParser();
    }

    /**
     * JobDetail - задача, будет создаваться с переодичностью,
     * работа этой задачи описываеться в классе GrabJob.
     * JobDataMap - класс карта (ключ/значение), для передачи
     * обьектов параметров в задачу. В классе задачи (GrabJob)
     * эти параметры извлекаються, через переданый параметр context.
     * SimpleScheduleBuilder - расписание переодичности запуска задачи.
     * Trigger - класс тригера, через него указываем как запускаеться задача.
     *
     * @param parse     обьект парсера html кода обьявлений.
     * @param store     обьект связи с БД.
     * @param scheduler
     * @throws SchedulerException
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data).build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.get("rabbit.interval")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
        try {
            Thread.sleep(5000);
            scheduler.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Внутриний класс задания. В методе execute()
     * получаем переданые обьекты через context.
     * В цикле перебора url-ов интерент страниц
     * по очереди извлекаються списки отпарсеных
     * обьявлений и передаються на запись в БД
     * во внутренем цикле.
     */
    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> posts; // все посты кладем в список
            for (String page : parse.pages()) {
                posts = parse.list(page);
                for (Post post : posts) { // выборка обьявлений
                    if (posts.isEmpty()) { // должно быть Условие записи в БД
                        break;
                    }
                    store.save(post);
                    System.out.println("save!");
                }
            }
        }
    }

    public static void main(String[] args) throws SchedulerException {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        Parse parse = grab.parse();
        grab.init(parse, store, scheduler);
    }
}
