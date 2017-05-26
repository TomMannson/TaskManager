package pl.tommmannson.taskqueue.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2017-05-05.
 */

public class CallForwarder {

    public static void forwardCall(Object target, String id, Task task) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

        String methodName = ste[3].getMethodName();
        try {
            Method method = target.getClass().getMethod(methodName, String.class, task.getClass());
            method.invoke(target, id, task);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
