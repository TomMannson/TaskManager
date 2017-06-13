package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface ErrorCallback {

    void onError(String id, Throwable ex);
}
