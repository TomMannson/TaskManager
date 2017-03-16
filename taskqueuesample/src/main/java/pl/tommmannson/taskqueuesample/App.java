package pl.tommmannson.taskqueuesample;

import android.app.Application;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.config.DefaultTaskManagerConfiguration;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class App extends Application {

    static JobManager JOB_MANAGER = null;

    @Override
    public void onCreate() {
        super.onCreate();

//        JOB_MANAGER = new JobManager(new Configuration.Builder(this)
//                .minConsumerCount(1)//always keep at least one consumer alive
//                .maxConsumerCount(3)//up to 3 consumers at a time
//                .loadFactor(3)//3 jobs per consumer
//                .consumerKeepAlive(120)
//                .build());


//        TaskManager taskManager = new TaskManager(new TaskManagerConfiguration.Builder()
////                .setClassOfService(TaskService.class)
//                .setMaxWorkerCount(4)
//                .setTaskMethodSerialisation(TaskManagerConfiguration.SQLITE_SERIALIZABLE)
//                .setThreadPoolMode(getApplicationContext())
//                .build());
//        taskManager.start(getApplicationContext());

        TaskManager taskManager2 = TaskManager.createInstance(1, new DefaultTaskManagerConfiguration(this));
        taskManager2.start(getApplicationContext());
    }
}
