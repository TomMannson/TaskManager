package pl.tommmannson.taskqueue.messaging.impl;

import pl.tommmannson.taskqueue.messaging.MessageHandler;

/**
 * Created by tomasz.krol on 2017-03-06.
 */

public class CancelTaskMessageHandler implements MessageHandler<CancelTaskMessage>{
    @Override
    public void handleMessage(CancelTaskMessage message) {
        message.getTaskManager().getService()
                .cancelRequest(message.getTask());
    }
}
