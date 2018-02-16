package pl.tommmannson.taskqueuesample;

import android.app.Application;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskSheduler;
import pl.tommmannson.taskqueue.config.DefaultTaskManagerConfiguration;
import pl.tommmannson.taskqueue.config.DefaultTaskSchedulerConfiguration;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TaskManager.createInstance(1, new DefaultTaskManagerConfiguration(this));
    }
}
