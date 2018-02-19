package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.messaging.Message;

/**
 * Created by tomasz.krol on 2017-03-06.
 */

public class CancelTaskMessage extends AbsMessage {

    private Task<?, ?> task;

    public Task<?, ?> getTask() {
        return task;
    }

    public void setTask(Task<?, ?> task) {
        this.task = task;
    }
}
