package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;

/**
 * 1) Конфигурирование и начало работы происходит с создания экземпляра класс планировщика
 * Scheduler, управляющего всеми работами. В объект Scheduler мы будем добавлять задачи,
 * которые хотим выполнять периодически.
 * 2) Создание задачи происходит в JobDetail.
 * 3) Создание расписания с указанием переодичности запуска происходит в SimpleScheduleBuilder.
 * 4) Задача выполняется через триггер. Тригер определяет когда запускаеться конкретное задание.
 * 5) После создания задачи и триггера, эти компоненты загружаються в планировщик.
 */
public class AlertRabbit {
    public static void main(String[] args) {
        ConfigValues config = new ConfigValues("rabbit.properties");
        int interval = Integer.parseInt(config.get("rabbit.interval"));
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval).repeatForever();
            Trigger trigger = newTrigger().startNow().withSchedule(times).build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Внутриний класс задачи, реализующий интерфейс Job.
     */
    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}
