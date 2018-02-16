package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class AddTaskMessage extends AbsMessage {

    Task<?, ?> task;
    Object[] data;

    public Task<?, ?> getTask() {
        return task;
    }

    public void setTask(Task<?, ?> task) {
        this.task = task;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
}
