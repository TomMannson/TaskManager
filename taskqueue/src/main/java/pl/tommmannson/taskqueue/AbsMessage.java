package pl.tommmannson.taskqueue;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.messaging.Message;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public abstract class AbsMessage implements Message {

    private final int type;

    public AbsMessage(int type){
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public TaskManagementInterface management() {
        return TaskManager.DEFAULT.service;
    }
}
