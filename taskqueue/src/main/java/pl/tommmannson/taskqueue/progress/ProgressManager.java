package pl.tommmannson.taskqueue.progress;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class ProgressManager<T> {

    List<TaskCallback<T>> callbacks = null;
    Handler handler = new Handler(Looper.getMainLooper());

    public ProgressManager(List<TaskCallback<T>> callback) {
        this.callbacks = callback;
    }

    public void postResult(final T data) {
        if (callbacks != null) {
            for (final TaskCallback callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "postResult "+callback.getClass().getSimpleName());
                        callback.onResult(data);
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

    public void onError(final Throwable ex) {
        if (callbacks != null) {
            for (final TaskCallback callback : callbacks) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ProgressManager", "onError "+callback.getClass().getSimpleName());
                        callback.onError(ex);
                    }
                });
            }
        }
    }
}