package pl.tommmannson.taskqueue.scheduler;

import pl.tommmannson.taskqueue.scheduler.impl.ExpRetryControler;
import pl.tommmannson.taskqueue.scheduler.impl.LinearRetryControler;
import pl.tommmannson.taskqueue.scheduler.impl.NoneRetryControler;

import static pl.tommmannson.taskqueue.scheduler.RetryControler.RETRY_STRATEGY_EXP;
import static pl.tommmannson.taskqueue.scheduler.RetryControler.RETRY_STRATEGY_LINEAR;

/**
 * Created by tomasz.krol on 2017-04-13.
 */

public class RetrySchedulerFactory {

    public RetryControler getRetrySheduler(RetryConfig config) {
        switch (config.getRetryStrategy()) {

            case RETRY_STRATEGY_LINEAR: {
                return new LinearRetryControler(config
                );
            }
            case RETRY_STRATEGY_EXP: {
                return new ExpRetryControler(config.getRetryLimit(),
                        config.getRetryMinTime(), config.getRetryMaxTime());
            }
            default: {
                return new NoneRetryControler();
            }

        }
    }
}
