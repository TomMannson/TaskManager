package pl.tommmannson.taskqueue.messaging;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public interface MessageHandler<T extends Message> {

    void handleMessage(T message);

}
