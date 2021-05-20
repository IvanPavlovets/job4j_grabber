package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.sql.*;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 1) Конфигурирование и начало работы происходит с создания экземпляра класс планировщика
 * Scheduler, управляющего всеми работами. В объект Scheduler мы будем добавлять задачи,
 * которые хотим выполнять периодически.
 * 2) Создание задачи происходит в JobDetail.
 * 3) В JobDataMap складываем обьекты(ключ/значение) для Job,
 * которые потом достаем с помощь context.
 * 4) Создание расписания с указанием переодичности запуска происходит в SimpleScheduleBuilder.
 * 5) Задача выполняется через триггер. Тригер определяет когда запускаеться конкретное задание.
 * 6) После создания задачи и триггера, эти компоненты загружаються в планировщик.
 * 7) В конце остановка планировщика - shutdown(). Время работы Job - Thread.sleep()
 */
public class AlertRabbit {

    public static void main(String[] args) {
        ConfigValues config = new ConfigValues("rabbit.properties");
        int interval = Integer.parseInt(config.get("rabbit.interval"));
        Scheduler scheduler = null;
        try (Connection cn = initConnection(config)) {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval).repeatForever();
            Trigger trigger = newTrigger().startNow().withSchedule(times).build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection initConnection(ConfigValues config) throws Exception {
        Connection cn = null;
        String url = config.get("rabbit.url");
        String login = config.get("rabbit.username");
        String password = config.get("rabbit.password");
        Class.forName(config.get("rabbit.driver-class-name"));
        cn = DriverManager.getConnection(url, login, password);
        if (cn != null) {
            System.out.println("Соединение успешно созданно! Можно добавлять данные");
        }
        return cn;
    }

    /**
     * Внутриний класс задачи, реализующий интерфейс Job.
     * Получаем объекты из context в методе execute().
     * Timestamp - время в милисекундах (Unix).
     * Date(stamp.getTime() - принимает милисекунды и конвертирует в дату.
     */
    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            String sql = "INSERT INTO rabbit(created_date) VALUES (?)";
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, stamp);
                ps.executeUpdate();
                System.out.println("Rabbit runs here ...");
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
