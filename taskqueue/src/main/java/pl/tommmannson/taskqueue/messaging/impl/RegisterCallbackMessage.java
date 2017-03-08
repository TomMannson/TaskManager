package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class RegisterCallbackMessage extends AbsMessage {
    private TaskManager taskManager;

    public RegisterCallbackMessage() {
        super(3);
    }

    Task task;
    TaskCallback taskCallback;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskCallback getTaskCallback() {
        return taskCallback;
    }

    public void setTaskCallback(TaskCallback<?> taskCallback) {
        this.taskCallback = taskCallback;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
