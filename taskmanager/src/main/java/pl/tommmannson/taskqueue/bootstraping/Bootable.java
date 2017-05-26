package pl.tommmannson.taskqueue.bootstraping;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.queues.TaskQueue;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public interface Bootable extends Runnable {
    Serializer getSerializer();

    Map<String,Task<?, ?>> getTasks();

    void setConcurrentTaskQueue(TaskQueue concurrentTaskQueue);

    void setTasksLoadedFlag(boolean b);

    int getWorkerThreadCount();

    void setWorkerThreadPool(ThreadPoolExecutor workerThreadPool);
}
