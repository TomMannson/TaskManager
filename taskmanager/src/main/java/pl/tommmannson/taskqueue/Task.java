package pl.tommmannson.taskqueue;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.persistence.RetryControler;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.ProgressManager;


/**
 * Created by Tomasz Król on 2015-11-13.
 */
public abstract class Task<T> implements Serializable {


    private String id;
    private long updateTime;
    private String groupId;
    private boolean persistent;
    RetryControler retry;
    private int priority = 0;

    private TaskResult<T> taskResult;
    private Throwable taskException;
    private TaskStatus taskStatus = TaskStatus.NotExistsInQueue;


    transient private TaskManager taskmanager;
    transient private ProgressManager<T> manager;

    protected Task(TaskParams params) {

        init(params);
    }

    public void init(TaskParams params) {
        if (params == null) {
            params = new TaskParams();
        }
        this.persistent = params.isPersistent();
        this.retry = new RetryControler(params.getRetryLimit());
        this.groupId = params.getGroupId();
        this.priority = params.getPriority();
    }

    public void run() {
        taskmanager.doTask(this);
    }

    public void cancel() {
        taskmanager.cancelRequest(this);
    }

    protected abstract void doWork(CancelationToken cancelToken) throws Exception;

    protected void recycle() {}

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String uuid) {
        id = uuid;
    }

    public TaskStatus getLastStatus() {
        return taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    synchronized void setTaskStatus(TaskStatus status) {
        updateTime = System.currentTimeMillis();
        taskStatus = status;
    }

    protected void notifyResult(TaskResult<T> data) {
        taskResult = data;
        manager.postResult(getId(), data);
        data.setTargetType(this.getClass());
    }

    protected void notifyError(Throwable ex) {
        taskException = ex;
        manager.onError(getId(), ex);
    }

    boolean nextRetry() {
        return retry.nextRetry();
    }

    void setTaskmanager(TaskManager taskmanager) {
        this.taskmanager = taskmanager;
        taskmanager.addTask(this);
    }

    void attachProgressManager(ProgressManager<T> manager) {
        this.manager = manager;
    }

    void detachProgressManager() {
        this.manager = null;
    }

    public T getTaskResult(TaskResult<T> resultData) {
        return resultData.getResultData();
    }

    public int getPriority() {
        return priority;
    }

    public String getGroupId() {
        return groupId;
    }

    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public int hashCode() {
        return this.getClass().getCanonicalName().hashCode();
    }

    @Override
    public boolean equals(Object otherObject) {

        if (otherObject == null || !(otherObject instanceof Task)) {
            return false;
        }

        Task taskToCheckEquality = (Task) otherObject;

        if (groupId != null) {
            return groupId.equals(taskToCheckEquality.groupId);
        }

        return super.equals(taskToCheckEquality);
    }

    @Override
    public String toString() {
        return String.format("Task %s, lastStatus %s", this.getClass().getSimpleName(), this.taskStatus.toString());
    }
}