package pl.tommmannson.taskqueue;

import pl.tommmannson.taskqueue.persistence.RetryControler;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class TaskParams {

    private boolean isUnique = false;
    private String groupId = null;
    private boolean persistent = false;
    private int retryLimit = 0;
    private int priority = 0;
    private int retryStrategy = RetryControler.RETRY_STRATEGY_NONE;


    public boolean isUnique() {
        return isUnique;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public int getRetryLimit() {
        return retryLimit;
    }

    public int getPriority() {
        return priority;
    }

    public int getRetryStrategy() {
        return retryStrategy;
    }

    public TaskParams unique(boolean unique) {
        isUnique = unique;
        return this;
    }

    public TaskParams groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public TaskParams persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public TaskParams retryLimit(int retryLimit) {
        this.retryLimit = retryLimit;
        return this;
    }

    public TaskParams retryStrategy(int retryStrategy) {
        this.retryStrategy = retryStrategy;
        return this;
    }

    public TaskParams priority(int priority) {
        this.priority = priority;
        return this;
    }


}
