package pl.tommmannson.taskqueue.bootstraping;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2016-12-06.
 */
public interface TaskManagementInterface {
    void addRequest(Task<?, ?> task, Object... data);

    void cancelRequest(Task<?, ?> request);

    void cancelAll();

    void registerCallbackForRequest(String requestId, TaskCallback callback);

    void unregisterCallbackForRequest(String requestId, TaskCallback callback);

    Task findTaskById(String id);

    void configure(TaskManagerConfiguration configuration);

    void start();

    void setQueueId(int queueId);

    void addTaskToTracking(Task task);

    void removeRequest(Task<?, ?> task);
}
