package pl.tommmannson.taskqueue.scheduler.impl;

import pl.tommmannson.taskqueue.scheduler.RetryControler;

/**
 * Created by tomasz.krol on 2017-04-13.
 */

public class NoneRetryControler extends RetryControler {
    public NoneRetryControler() {
        super(0, 0, 0);
    }

    @Override
    public int timeChange(int actual) {
        return 0;
    }
}
