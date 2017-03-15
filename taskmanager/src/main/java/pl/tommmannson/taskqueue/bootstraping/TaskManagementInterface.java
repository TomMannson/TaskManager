package pl.tommmannson.taskqueue.bootstraping;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2016-12-06.
 */
public interface TaskManagementInterface {
    void addRequest(Task<?> task);

    void cancelRequest(Task<?> request);

    void cancelAll();

    <T> void registerCallbackForRequest(Task<T> request, TaskCallback callback);

    <T> void unregisterCallbackForRequest(Task<T> request, TaskCallback callback);

    <T> TaskStatus getTaskStatus(Task<T> request);

    Task findTaskById(String id);

    void configure(TaskManagerConfiguration configuration);

    void start();

    void setQueueId(int queueId);

    boolean tasksLoaded();
}
