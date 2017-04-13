package pl.tommmannson.taskqueue.scheduler;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public abstract class RetryControler implements Serializable {

    private static final int RETRY_STOP = 0;
    private static final int RETRY_INFINITY = -1;

    public static final int RETRY_STRATEGY_NONE = 0;
    public static final int RETRY_STRATEGY_LINEAR = 1;
    public static final int RETRY_STRATEGY_EXP = 2;

    protected final int retryMaxTime;
    protected final int retryMinTime;
    private int retryLimit = 0;
    private int retryTime = 100;

    protected RetryControler(int retryLimit, int retryMinTime
            ,int retryMaxTime) {
        this.retryLimit = retryLimit;
        this.retryLimit = retryLimit;
        this.retryTime = retryMinTime;
        this.retryMinTime = retryMinTime;
        this.retryMaxTime = retryMaxTime;
    }

    public boolean nextRetry() {
        if (retryLimit > RETRY_STOP) {
            retryLimit--;
            retryTime = timeChange(retryTime);//2;
            if(retryTime > retryMaxTime){
                retryTime = retryMaxTime;
            }
            return true;
        } else return retryLimit == RETRY_INFINITY;
    }


    public abstract int timeChange(int actual);

    public long retryDelay(){
        return retryTime;
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

    public int getRetryTime() {
        return retryTime;
    }
}
