package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.messaging.MessageHandler;
import pl.tommmannson.taskqueue.utils.TaskQueueReadyChecker;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class RegisterCallbackMessageHandler implements MessageHandler<RegisterCallbackMessage> {

    @Override
    public void handleMessage(RegisterCallbackMessage message) {

        TaskQueueReadyChecker.waitForreadyQueue(message.getTaskManager());

        message.getTaskManager().getService().registerCallbackForRequest(message.getTask(), message.getTaskCallback());

    }
}
