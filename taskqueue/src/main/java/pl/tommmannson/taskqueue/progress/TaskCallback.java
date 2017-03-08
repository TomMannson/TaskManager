package pl.tommmannson.taskqueue.progress;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface TaskCallback <T> {

    void onResult(T result);
    void onError(Throwable ex);
}
