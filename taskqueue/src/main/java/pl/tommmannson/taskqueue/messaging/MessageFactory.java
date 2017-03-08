package pl.tommmannson.taskqueue.messaging;

/**
 * Created by tomasz.krol on 2016-12-06.
 */


public class MessageFactory {

    public <T extends Message> T obtain(Class<T> klass) {

        try {
            return klass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
