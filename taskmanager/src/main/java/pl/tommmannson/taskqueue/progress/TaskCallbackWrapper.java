package pl.tommmannson.taskqueue.progress;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;

/**
 * Created by tomasz.krol on 2017-06-13.
 */

public class TaskCallbackWrapper<Result, Progress> implements TaskCallback<Result, Progress> {

    private ResultCallback<Result> resultListener;
    private ProgressCallback<Progress> progressListener;
    private ErrorCallback errorListener;

    public TaskCallbackWrapper(ResultCallback<Result> resultListener,
                               ProgressCallback<Progress> progressListener,
                               ErrorCallback errorListener) {

        this.resultListener = resultListener;
        this.progressListener = progressListener;
        this.errorListener = errorListener;
    }

    @Override
    public void onError(String id, Throwable ex) {
        if (errorListener != null) {
            errorListener.onError(id, ex);
        }
    }

    @Override
    public void onResult(Task<Result, ?> task) {
        if (resultListener != null) {
            resultListener.onResult(task);
        }
    }

    @Override
    public void onProgress(String id, TaskResult<Progress> result) {
        if (progressListener != null) {
            progressListener.onProgress(id, result);
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof TaskCallbackWrapper) {
            TaskCallbackWrapper wraperToCheck = (TaskCallbackWrapper) obj;

            return wraperToCheck.resultListener == this.resultListener &&
                    wraperToCheck.progressListener == this.progressListener &&
                    wraperToCheck.errorListener == this.errorListener;
        }
        return super.equals(obj);
    }
}
