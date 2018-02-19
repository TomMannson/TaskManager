package pl.tommmannson.taskqueue.messaging;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by tomasz.krol on 2016-12-06.
 */


public class MessageFactory {

    private Map<Class<?>, Stack> cachedMessages = new HashMap<>();

    public synchronized <T extends Message> T obtain(Class<T> klass) {

        Stack queue = cachedMessages.get(klass);
        T message = null;//klass.newInstance();
        try {

            if (queue == null) {
                queue = new Stack();
                cachedMessages.put(klass, queue);
                return klass.newInstance();
            } else {
                try {
                    message = (T) queue.pop();

                } catch (EmptyStackException ex) {
                    message = klass.newInstance();
                }
                return message;
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    synchronized <T extends Message> void release(Message message) {

        Stack queue = cachedMessages.get(message.getClass());
        queue.push(message);
    }

}
