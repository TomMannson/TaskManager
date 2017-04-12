package pl.tommmannson.taskqueue.persistence;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public class RetryControler implements Serializable {

    private static final int RETRY_STOP = 0;
    private static final int RETRY_INFINITY = -1;

    private int retryLimit = 0;

    public RetryControler(int retryLimit) {
        this.retryLimit = retryLimit;
    }

    public boolean nextRetry() {
        if (retryLimit > RETRY_STOP) {
            retryLimit--;
            return true;
        } else if (retryLimit == RETRY_INFINITY) {
            return true;
        } else {
            return false;
        }
    }
}
