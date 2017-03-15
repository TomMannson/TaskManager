package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.TaskResult;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface TaskCallback {

    void onResult(String id, TaskResult result);
    void onError(String id, Throwable ex);
}
