package pl.tommmannson.taskqueue;

import pl.tommmannson.taskqueue.scheduler.RetryConfig;
import pl.tommmannson.taskqueue.scheduler.RetryControler;
import pl.tommmannson.taskqueue.scheduler.impl.NoneRetryControler;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class TaskParams {

    private String groupId = null;
    private boolean persistent = false;
    private int priority = 0;
    private RetryConfig retryConfig = new RetryConfig();

    public String getGroupId() {
        return groupId;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public int getPriority() {
        return priority;
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
        this.retryConfig.retryLimit(retryLimit);
        return this;
    }

    public TaskParams retryStrategy(int retryStrategy) {
        this.retryConfig.retryStrategy(retryStrategy);
        return this;
    }

    public TaskParams retryMinTime(int retryMinTime) {
        this.retryConfig.retryMinTime(retryMinTime);
        return this;
    }

    public TaskParams retryMaxTime(int retryMaxTime) {
        this.retryConfig.retryMaxTime(retryMaxTime);
        return this;
    }

    public TaskParams priority(int priority) {
        this.priority = priority;
        return this;
    }


}
