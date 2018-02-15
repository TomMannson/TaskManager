package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.messaging.MessageHandler;
import pl.tommmannson.taskqueue.utils.TaskQueueReadyChecker;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class DeleteTaskMessageHandler implements MessageHandler<DeleteTaskMessage> {

    @Override
    public void handleMessage(DeleteTaskMessage message) {

        TaskQueueReadyChecker.waitForreadyQueue(message.getTaskManager());
        message.getTaskManager().getService().removeRequest(message.getTask());

    }
}
