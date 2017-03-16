package pl.tommmannson.taskqueue;


import android.support.annotation.NonNull;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.UUID;

import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.ProgressManager;


/**
 * Created by Tomasz Król on 2015-11-13.
 */
public abstract class Task<T> implements Serializable {

    private String id;
    private boolean isUnique;
    private String groupId;
    private boolean persistent;
    private int retryLimit = 0;
    private int priority = 0;

    transient private boolean isAttached = false;
    private TaskStatus taskStatus = TaskStatus.NotExistsInQueue;
    transient private ProgressManager<T> manager;
    private boolean created = false;

    protected Task(TaskParams params) {
        if(params == null){
            params = new TaskParams();
        }
        this.persistent = params.isPersistent();
        this.isUnique = params.isUnique();
        this.retryLimit = params.getRetryLimit();
        this.groupId = params.getGroupId();
        this.priority = params.getPriority();
    }

    public void run(){

        TaskManager manager = TaskManager.DEFAULT;
        if(manager != null){
            manager.doTask(this);
        }
        else{
            throw new IllegalStateException("DEFAULT static field in TaskManager class has to be set");
        }
    }

    public void run(int managerId){

        if(managerId == 0){
            run();
        }
        else{
            TaskManager manager = TaskManager.instances.get(managerId);
            if(manager == null){
                throw new IllegalStateException("TaskManager with id " + managerId + "doesn't exists");
            }
            manager.doTask(this);
        }
    }

    public void cancel(){
        TaskManager manager = TaskManager.DEFAULT;
        if(manager != null){
            manager.cancelRequest(this);
        }
        else{
            throw new IllegalStateException("DEFAULT static field in TaskManager class has to be set");
        }
    }

    public void cancel(int managerId){
        if(managerId == 0){
            run();
        }
        else{
            TaskManager manager = TaskManager.instances.get(managerId);
            if(manager == null){
                throw new IllegalStateException("TaskManager with id " + managerId + "doesn't exists");
            }
            manager.cancelRequest(this);
        }
    }

    protected abstract void doWork(CancelationToken cancelToken)  throws Exception;

    protected void recycle(){}

    public String getGroupId() {
        return groupId;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean isUnique() {
        return isUnique;
    }

    @NonNull public String getId(){

        if(id == null){
            id = UUID.randomUUID().toString();
        }

        return id;
    }

    public void setId(String uuid){
        id = uuid;
    }

    boolean nextRetry(){
        if(retryLimit > 0){
            retryLimit--;
            return true;
        }
        else if (retryLimit == -1){
            return true;
        }
        else{
            return false;
        }
    }

    public TaskStatus getTaskStatus(){
        if(!isAttached){
            TaskManager manager = TaskManager.DEFAULT;
            if(manager != null){
                taskStatus = manager.checkExecutionStatus(this);
                return taskStatus;
            }
            else {
                return TaskStatus.NotExistsInQueue;
            }
        }

        return taskStatus;
    }

    public TaskStatus getLastStatus(){
        return taskStatus;
    }

    public TaskStatus getTaskStatus(int managerId){
        if(!isAttached){
            TaskManager manager = TaskManager.getInstance(managerId);
            if(manager != null){
                taskStatus = manager.checkExecutionStatus(this);
                return taskStatus;
            }
            else {
                return TaskStatus.NotExistsInQueue;
            }
        }

        return taskStatus;
    }

    synchronized void setTaskStatus(TaskStatus status){
        taskStatus = status;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public int getPriority() {
        return priority;
    }

    void setIsAttached() {
        isAttached = true;
    }

    void detachProgressManager() {
        this.manager = null;
    }

    protected void notifyResult(TaskResult<T> data){
        manager.postResult(getId(), data);
        data.setTargetType(this.getClass());
    }

    protected void notifyError(Throwable ex){
        manager.onError(getId(), ex);
    }

    void attachProgressManager(ProgressManager<T> manager) {
        this.manager = manager;
    }

    @Override
    public int hashCode() {
        return this.getClass().getCanonicalName().hashCode();
    }

    @Override
    public boolean equals(Object otherObject) {

        if(otherObject == null || !(otherObject instanceof Task) ){
            return false;
        }

        Task taskToCheckEquality = (Task)otherObject;

        if(groupId != null) {
            return groupId.equals(taskToCheckEquality.groupId);
        }

        if(isUnique){
            String thisClassName = this.getClass().getCanonicalName();
            String requestClassName = taskToCheckEquality.getClass().getCanonicalName();
            return thisClassName.equals(requestClassName);
        }

        return super.equals(taskToCheckEquality);
    }

    @Override
    public String toString() {
        return String.format("Task %s, lastStatus %s", this.getClass().getSimpleName(), this.taskStatus.toString());
    }

    public T getTaskResult(TaskResult resultData) {
        return (T) resultData.getResultDataRaw();
    }
}