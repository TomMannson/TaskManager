package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.messaging.MessageHandler;
import pl.tommmannson.taskqueue.utils.TaskQueueReadyChecker;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class UnregisterCallbackMessageHandler implements MessageHandler<UnregisterCallbackMessage> {

    @Override
    public void handleMessage(UnregisterCallbackMessage message) {

        TaskQueueReadyChecker.waitForreadyQueue(message.getTaskManager());
        message.getTaskManager().getService()
                .unregisterCallbackForRequest(message.getTask(), message.getTaskCallback());
    }
}
