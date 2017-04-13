package pl.tommmannson.taskqueue.scheduler.impl;

import pl.tommmannson.taskqueue.scheduler.RetryControler;

/**
 * Created by tomasz.krol on 2017-04-13.
 */

public class ExpRetryControler extends RetryControler {
    public ExpRetryControler(int limit, int min, int max) {
        super(limit, min, max);
    }

    @Override
    public int timeChange(int actual) {
        return actual * 2;
    }
}
