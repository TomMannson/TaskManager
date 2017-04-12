package pl.tommmannson.taskqueue.persistence;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public class RetryControler implements Serializable {

    private static final int RETRY_STOP = 0;
    private static final int RETRY_INFINITY = -1;

    public static final int RETRY_STRATEGY_NONE = 0;
    public static final int RETRY_STRATEGY_LINEAR = 1;
    public static final int RETRY_STRATEGY_EXP = 2;

    private int retryLimit = 0;
    private int retryStrategy = 0;
    private int retryTime = 100;

    public RetryControler(int retryLimit, int retryStrategy) {
        this.retryLimit = retryLimit;
        this.retryStrategy = retryStrategy;
    }

    public boolean nextRetry() {
        if (retryLimit > RETRY_STOP) {
            retryLimit--;
            retryTime *= 2;
            return true;
        } else if (retryLimit == RETRY_INFINITY) {
            return true;
        } else {
            return false;
        }
    }

    public long retryDelay(){
        return retryTime;
    }
}
