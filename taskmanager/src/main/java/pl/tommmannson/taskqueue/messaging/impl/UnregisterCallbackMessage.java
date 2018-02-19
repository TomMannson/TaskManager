package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class UnregisterCallbackMessage extends AbsMessage {

    private String taskId;
    private TaskCallback taskCallback;

    String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    TaskCallback getTaskCallback() {
        return taskCallback;
    }

    public void setTaskCallback(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
    }
}
