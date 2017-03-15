package pl.tommmannson.taskqueue.progress;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class ProgressManagerFactory {

    public static <T> ProgressManager<T> create(String requestId, Map<String, List<TaskCallback>> callbacks) {
        List<TaskCallback> obj = callbacks.get(requestId);

        if (obj == null) {
            obj = new ArrayList<>();
            callbacks.put(requestId, obj);
        }

        return new ProgressManager<T>(obj);
    }
}
