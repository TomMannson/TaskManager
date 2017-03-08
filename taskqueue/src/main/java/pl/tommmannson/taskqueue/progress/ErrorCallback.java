package pl.tommmannson.taskqueue.progress;

/**
 * Created by tomasz.krol on 2016-12-07.
 */

public interface ErrorCallback {

    void onError(Throwable ex);
}
