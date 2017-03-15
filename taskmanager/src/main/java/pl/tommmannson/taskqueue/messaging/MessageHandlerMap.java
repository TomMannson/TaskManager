package pl.tommmannson.taskqueue.messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class MessageHandlerMap {

    private Map<Class<? extends Message>, MessageHandler<? extends Message>> map = new HashMap<>();


    public <T extends Message> void put(Class<T> message, MessageHandler<T> handler){
        map.put(message, handler);
    }

    public <T extends Message> MessageHandler<T> get(Class<T> message){
        return (MessageHandler<T>) map.get(message);
    }

}
