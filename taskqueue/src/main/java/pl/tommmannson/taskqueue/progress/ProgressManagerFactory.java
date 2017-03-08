package pl.tommmannson.taskqueue.progress;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class ProgressManagerFactory {

    public static <T> ProgressManager<T> create(Task<T> request, Map<Task<?>, List<TaskCallback>> callbacks) {
        Object obj = callbacks.get(request);
        if (obj == null) {
            obj = new ArrayList<TaskCallback<T>>();
            callbacks.put(request, (List<TaskCallback>) obj);
        }

        return new ProgressManager<T>((List<TaskCallback<T>>) obj);
    }
}
