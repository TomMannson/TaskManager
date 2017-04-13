package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface TaskCallback<T extends Task> {

    void onResult(String id, T task);
    void onError(String id, Throwable ex);
}
