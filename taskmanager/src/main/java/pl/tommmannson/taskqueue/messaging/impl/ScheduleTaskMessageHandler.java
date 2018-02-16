package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.messaging.MessageHandler;
import pl.tommmannson.taskqueue.utils.TaskQueueReadyChecker;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class ScheduleTaskMessageHandler implements MessageHandler<ScheduleTaskMessage> {

    @Override
    public void handleMessage(ScheduleTaskMessage message) {

        TaskQueueReadyChecker.waitForreadyQueue(message.getTaskManager());
        message.getTaskManager().getService().addRequest(message.getTask());

    }
}
