package pl.tommmannson.taskqueue.scheduler;

/**
 * Created by tomasz.krol on 2017-04-13.
 */

public class RetryConfig {
    private int retryMaxTime;
    private int retryMinTime;
    private int retryLimit = 0;
    private int retryStrategy;

    public RetryConfig retryMaxTime(int retryMaxTime) {
        this.retryMaxTime = retryMaxTime;
        return this;
    }

    public RetryConfig retryMinTime(int retryMinTime) {
        this.retryMinTime = retryMinTime;
        return this;
    }

    public RetryConfig retryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    public RetryConfig retryStrategy(int retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public int getRetryMaxTime() {
        return retryMaxTime;
    }

    public int getRetryMinTime() {
        return retryMinTime;
    }

    public int getRetryLimit() {
        return retryLimit;
    }

    public int getRetryStrategy() {
        return retryStrategy;
    }
}
