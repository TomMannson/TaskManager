package pl.tommmannson.taskqueue.scheduler;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public interface RetryOperation {

    void doOnRetry();
}
