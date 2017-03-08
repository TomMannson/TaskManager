package pl.tommmannson.taskqueue.progress;

/**
 * Created by tomasz.krol on 2016-12-07.
 */

public interface ResultCallback<T> {

    void onResult(T result);
}
