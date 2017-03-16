package pl.tommmannson.taskqueuesample;

import android.app.Application;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskService;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TaskManager taskManager = new TaskManager(new TaskManagerConfiguration.Builder()
//                .setClassOfService(TaskService.class)
                .setMaxWorkerCount(4)
                .setTaskMethodSerialisation(TaskManagerConfiguration.SQLITE_SERIALIZABLE)
                .setThreadPoolMode(getApplicationContext())
                .build());
        taskManager.start(getApplicationContext());

        TaskManager taskManager2 = TaskManager.createInstance(1, new TaskManagerConfiguration.Builder()
//                .setClassOfService(TaskService.class)
                .setMaxWorkerCount(4)
                .setTaskMethodSerialisation(TaskManagerConfiguration.SQLITE_SERIALIZABLE)
                .setThreadPoolMode(getApplicationContext())
                .build());
        taskManager2.start(getApplicationContext());
    }
}
