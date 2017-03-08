package pl.tommmannson.taskqueue.bootstraping;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.queues.ConcurrentTaskQueue;
import pl.tommmannson.taskqueue.queues.TaskQueue;
import pl.tommmannson.taskqueue.utils.TaskStatusHelper;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class Bootstraper {

    public static void start(final Bootable bootstrapable) {
//        new Thread() {
//            public void run() {

        Serializer serializer = bootstrapable.getSerializer();
        TaskQueue concurrentTaskQueue = new ConcurrentTaskQueue(serializer);
        Map<String, Task<?>> tasks = bootstrapable.getTasks();

        bootstrapable.setConcurrentTaskQueue(concurrentTaskQueue);
        serializer.restore(concurrentTaskQueue);

        for (Task task : concurrentTaskQueue.getFullList()) {
            tasks.put(task.getId(), task);

            if (TaskStatusHelper.taskAfterExecution(task.getTaskStatus()) ||
                    task.getTaskStatus() == TaskStatus.Canceled) {
                concurrentTaskQueue.removeWaiting(task);
            }
        }

        bootstrapable.setTasksLoadedFlag(true);
        int threadPoolSize = bootstrapable.getWorkerThreadCount();
        ThreadPoolExecutor workerThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Task Worker Thread");
            }
        });
        bootstrapable.setWorkerThreadPool(workerThreadPool);

        workerThreadPool.execute(bootstrapable);
//            }
//        }.start();
    }

}
