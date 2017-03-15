package pl.tommmannson.taskqueue.config;


import pl.tommmannson.taskqueue.TaskService;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class DefaultTaskManagerConfiguration extends TaskManagerConfiguration {
    public DefaultTaskManagerConfiguration(Class<? extends TaskService> classOfService) {
        super(classOfService, null, 3, 1);
    }
}
