package pl.tommmannson.taskqueue.persistence;

import java.io.Serializable;

import pl.tommmannson.taskqueue.TaskResult;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public class TaskState implements Serializable {

    TaskStatus status = TaskStatus.NotExistsInQueue;
    TaskResult<?> result = null;
    Exception exception = null;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskResult<?> getResult() {
        return result;
    }

    public void setResult(TaskResult<?> result) {
        this.result = result;
        this.exception = null;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        this.result = null;
    }
}