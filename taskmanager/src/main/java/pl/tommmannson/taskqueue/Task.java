package pl.tommmannson.taskqueue;


//import android.support.annotation.NonNull;

import android.app.job.JobInfo;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.progress.ErrorCallback;
import pl.tommmannson.taskqueue.progress.ResultCallback;
import pl.tommmannson.taskqueue.progress.TaskCallbackWrapper;
import pl.tommmannson.taskqueue.scheduler.RetryConfig;
import pl.tommmannson.taskqueue.scheduler.RetryControler;
import pl.tommmannson.taskqueue.persistence.TaskState;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.ProgressManager;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.scheduler.RetrySchedulerFactory;

import static pl.tommmannson.taskqueue.TaskScheduler.SCHEDULER_ID;


/**
 * Created by Tomasz Król on 2015-11-13.
 */
public abstract class Task<T extends Serializable, Progress> implements Serializable {


    private String id;
    private long updateTime;
    private String groupId;
    private boolean persistent;
    private RetryControler retry;
    private int priority = 0;

    private TaskState<T> state = new TaskState<>();

    transient private TaskManager taskmanager;
    transient private ProgressManager<T, Progress> manager;

    public Task(TaskParams params) {

        init(params);
    }

    private void init(TaskParams params) {
        RetrySchedulerFactory factory = new RetrySchedulerFactory();
        if (params == null) {
            params = new TaskParams();
        }
        this.persistent = params.isPersistent();
        this.retry = factory.getRetrySheduler(params.getRetryConfig());
        this.groupId = params.getGroupId();
        this.priority = params.getPriority();
    }

    public void run() {
        taskmanager.doTask(this);
    }

    public void schedule(JobInfo.Builder builder) {
        if(this.taskmanager.getId() != SCHEDULER_ID){
            throw new InvalidParameterException("task created by TaskManager can't be scheduled");
        }
        taskmanager.doTask(this, builder);
    }

    public void cancel() {
        taskmanager.cancelRequest(this);
    }

//    public void delete() {
//        taskmanager.remove(this);
//    }

    public void register(final TaskCallback<T, Progress> callback){
        taskmanager.registerCallback(getId(), callback);
    }

    public void unregister(final TaskCallback<T, Progress> callback){
        taskmanager.unregisterCallback(getId(), callback);
    }

    public void registerPartial(ResultCallback<T> result){
        TaskCallbackWrapper<T, Progress> wrapper
                = new TaskCallbackWrapper<>(result, null, null);

        taskmanager.registerCallback(getId(), wrapper);
    }

    public void unregisterPartial(ResultCallback<T> result){

        TaskCallbackWrapper<T, Progress> wrapper
                = new TaskCallbackWrapper<>(result, null, null);

        taskmanager.unregisterCallback(getId(), wrapper);
    }

    public void registerPartial(ResultCallback<T> result, ErrorCallback errorCallback){
        TaskCallbackWrapper<T, Progress> wrapper
                = new TaskCallbackWrapper<>(result, null, errorCallback);

        taskmanager.registerCallback(getId(), wrapper);
    }

    public void unregisterPartial(ResultCallback<T> result, ErrorCallback errorCallback){

        TaskCallbackWrapper<T, Progress> wrapper
                = new TaskCallbackWrapper<>(result, null, errorCallback);

        taskmanager.unregisterCallback(getId(), wrapper);
    }

    protected abstract void doWork(CancelationToken cancelToken) throws Exception;

    void recycle() {
    }

//    @NonNull
    public String getId() {
        return id;
    }

    int getManagerId(){
        return taskmanager.getId();
    }

    public void setId(String uuid) {
        id = uuid;
    }

    public TaskState<T> getState() {
        return state;
    }

    synchronized void setExecutionStatus(TaskStatus status) {
        state.setStatus(status);
        updateTime = System.currentTimeMillis();
    }

    protected void notifyResult(T data) {
        state.setResult(data);//taskResult = data;
    }

    protected void notifyProgress(TaskResult<Progress> progress) {
        manager.postProgress(getId(), progress);
    }

    void sendResult() {
        manager.postResult(this);
    }

    protected void notifyError(Throwable ex) {
        state.setException((Exception) ex);
        manager.onError(this);
    }

    boolean nextRetry() {
        return retry.nextRetry();
    }

    public RetryControler getRetryState() {
        return retry;
    }

    void setTaskmanager(TaskManager taskmanager) {
        this.taskmanager = taskmanager;
        taskmanager.addTask(this);
    }

    void attachProgressManager(ProgressManager<T, Progress> manager) {
        this.manager = manager;
    }

    void detachProgressManager() {
        this.manager = null;
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
        return String.format("Task %s, lastStatus %s", this.getClass().getSimpleName(), this.state.getStatus().toString());
    }

    ProgressManager<T, Progress> createProgressManager(String requestId, Map<String, List> callbacks) {
//        Object downCast = callbacks.get(requestId);
        List<TaskCallback<T, Progress>> obj = callbacks.get(requestId);//callbacks.get(requestId);

        if (obj == null) {
            obj = new ArrayList<>();
            callbacks.put(requestId, obj);
        }

        return new ProgressManager<>(obj);
    }
}
