package pl.tommmannson.taskqueue.scheduler.impl;

import pl.tommmannson.taskqueue.scheduler.RetryConfig;
import pl.tommmannson.taskqueue.scheduler.RetryControler;

/**
 * Created by tomasz.krol on 2017-04-13.
 */

public class LinearRetryControler extends RetryControler {
    public LinearRetryControler(RetryConfig config) {
        super(config.getRetryLimit(), config.getRetryMinTime(),
                config.getRetryMaxTime());
    }

    @Override
    public int timeChange(int actual) {
        return actual + retryMinTime;
    }
}
