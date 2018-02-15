package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.AbsMessage;
import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class DeleteTaskMessage extends AbsMessage {

    public DeleteTaskMessage() {
        super(2);
    }

    Task<?, ?> task;

    public Task<?, ?> getTask() {
        return task;
    }

    public void setTask(Task<?, ?> task) {
        this.task = task;
    }
}
