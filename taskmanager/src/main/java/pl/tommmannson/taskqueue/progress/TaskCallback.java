package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.persistence.TaskState;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface TaskCallback {

    void onResult(String id, TaskState state);
    void onError(String id, Throwable ex);
}
