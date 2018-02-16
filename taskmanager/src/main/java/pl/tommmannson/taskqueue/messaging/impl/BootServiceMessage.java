package pl.tommmannson.taskqueue.messaging.impl;

import android.content.Context;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class BootServiceMessage extends AbsMessage {

    private TaskManagerConfiguration config;
    private Context appContext;

    public TaskManagerConfiguration getConfig() {
        return config;
    }

    public void setConfig(TaskManagerConfiguration config) {
        this.config = config;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }
}
