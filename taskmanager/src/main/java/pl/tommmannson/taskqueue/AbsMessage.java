package pl.tommmannson.taskqueue;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.messaging.Message;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public abstract class AbsMessage implements Message {

    private TaskManager taskManager;

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
