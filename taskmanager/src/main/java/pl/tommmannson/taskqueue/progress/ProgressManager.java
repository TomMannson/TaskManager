package pl.tommmannson.taskqueue.progress;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskState;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class ProgressManager<Result extends Serializable, Progress> {

    List<TaskCallback<Result, Progress>> callbacks = null;
    Handler handler = new Handler(Looper.getMainLooper());

    public ProgressManager(List<TaskCallback<Result, Progress>> callback) {
        this.callbacks = callback;
    }

    public void postResult(final Task<Result, Progress> task) {
        if (callbacks != null) {
            for (final TaskCallback callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "postResult "+callback.getClass().getSimpleName());
                        callback.<Result>onResult(task);
                    }
                });
            }
        }
    }

    public void onError(final Task<Result, Progress> task) {
        if (callbacks != null) {
            for (final TaskCallback callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "onError "+callback.getClass().getSimpleName());
                        callback.onError(task.getId(), task.getState().getException());
                    }
                });
            }
        }
    }

    public void postProgress(final String id, final TaskResult<Progress> tProgressTask) {
        if (callbacks != null) {
            for (final TaskCallback callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "onError "+callback.getClass().getSimpleName());
                        callback.onProgress(id, tProgressTask);
                    }
                });
            }
        }
    }
}