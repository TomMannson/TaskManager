package pl.tommmannson.taskqueue.messaging.impl;

import android.content.Context;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class BootServiceMessage extends AbsMessage {
    private TaskManager manager;
    private TaskManagerConfiguration config;
    private Context appContext;

    public BootServiceMessage() {
        super(0);
    }

    public TaskManager getManager() {
        return manager;
    }

    public void setManager(TaskManager manager) {
        this.manager = manager;
    }

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
