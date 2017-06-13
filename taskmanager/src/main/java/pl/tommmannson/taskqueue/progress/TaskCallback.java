package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface TaskCallback<Result, Progress> extends ErrorCallback,
        ProgressCallback<Progress>,
        ResultCallback<Result>
{

    void onResult(Task<Result, ?> task);
    void onProgress(String id, TaskResult<Progress> result);
    void onError(String id, Throwable ex);
}
