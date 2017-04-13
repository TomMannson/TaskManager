package pl.tommmannson.taskqueue.progress;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.persistence.TaskState;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class ProgressManager<T extends Task> {

    List<TaskCallback> callbacks = null;
    Handler handler = new Handler(Looper.getMainLooper());

    public ProgressManager(List<TaskCallback> callback) {
        this.callbacks = callback;
    }

    public void postResult(final T task) {
        if (callbacks != null) {
            for (final TaskCallback<T> callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "postResult "+callback.getClass().getSimpleName());
                        callback.<T>onResult(task.getId(), task);
                    }
                });
            }
        }
    }
//
//    public void postSuccess() {
//        if (callbacks != null) {
//            for (final TaskCallback callback : callbacks) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("ProgressManager", "postSuccess "+callback.getClass().getSimpleName());
//                        callback.onSuccess();
//                    }
//                });
//            }
//        }
//    }

    public void onError(final Task<T> task) {
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




}