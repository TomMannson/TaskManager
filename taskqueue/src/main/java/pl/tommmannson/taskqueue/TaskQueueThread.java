package pl.tommmannson.taskqueue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import pl.tommmannson.taskqueue.bootstraping.Bootable;
import pl.tommmannson.taskqueue.bootstraping.Bootstraper;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.cancelation.CancelationException;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.di.DependencyInjector;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.persistence.sqlite.SqlSerializer;
import pl.tommmannson.taskqueue.progress.ProgressManager;
import pl.tommmannson.taskqueue.progress.ProgressManagerFactory;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.queues.TaskQueue;
import pl.tommmannson.taskqueue.utils.Logger;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class TaskQueueThread implements Bootable, TaskManagementInterface {

    static public final Class<?> TAG = TaskQueueThread.class;

    private Map<Task<?>, List<TaskCallback>> callbacks = new HashMap<>();
    final private Map<Task<?>, CancelationToken> cancelation = new HashMap<>();
    final private Map<String, Task<?>> tasks = new HashMap<>();
    private TaskQueue concurrentTaskQueue = null;
    private Serializer serializer;

    int workerThreadCount;
    DependencyInjector injector = null;
    ThreadPoolExecutor workerThreadPool = null;
    private boolean tasksLoadedFlag;
    private boolean logger;

    public TaskQueueThread(Context ctx){
        serializer = new SqlSerializer(ctx.getApplicationContext());
    }

    public void start() {
        Bootstraper.start(this);
    }

    @Override
    public void run() {

        if (concurrentTaskQueue.hasTasksWaitingForExecution()) {
            Task request = null;
//            manager = null;
            try {

                request = concurrentTaskQueue.getTaskFromQueue();

                if (request == null) {
                    return;
                }

                performTaskWork(request);

                concurrentTaskQueue.removeProcessing(request);

            } catch (InterruptedException ex) {
                concurrentTaskQueue.moveFromExecutingToWaiting(request);
                serializer.persist(concurrentTaskQueue, request);
            } catch (CancelationException ex) {
                Log.d(this.getClass().getName(), String.format("%s canceled", request));

                ProgressManager manager = ProgressManagerFactory.create(request, callbacks);
                manager.postResult(TaskResult.cancelResult(null));

                if (request != null) {
                    cancelation.remove(request);
                    concurrentTaskQueue.removeProcessing(request);
                    request.setTaskStatus(TaskStatus.Canceled);
                }
                serializer.persist(concurrentTaskQueue, request);
            } catch (Exception ex) {

                Log.e("TaskService", "Exception during request process on  ");
                ex.printStackTrace();

                if (request != null) {
                    if (request.nextRetry()) {
                        concurrentTaskQueue.moveFromExecutingToWaiting(request);
                        workerThreadPool.execute(this);
                    } else {
                        concurrentTaskQueue.removeProcessing(request);
                    }

                    request.setTaskStatus(TaskStatus.FailFinished);
                    request.recycle();

                }
            } finally {
                if (request != null) {
                    request.detachProgressManager();
                    serializer.persist(concurrentTaskQueue, request);
                }
            }

            workerThreadPool.execute(this);
        }
    }

    private void performTaskWork(Task request) throws Exception {
        CancelationToken token = new CancelationToken();
        cancelation.put(request, token);
        ProgressManager manager = ProgressManagerFactory.create(request, callbacks);

        request.setTaskStatus(TaskStatus.InProgress);

        if (injector != null) {
            injector.inject(request);
        }
        request.attachProgressManager(manager);
        request.doWork(token);
        cancelation.remove(request);

        request.setTaskStatus(TaskStatus.SuccessfullyFinished);
    }

    public void addRequest(Task<?> task) {

        if (concurrentTaskQueue.pushToQueue(task)) {
            task.setIsAttached();
            tasks.put(task.getId(), task);
            task.setTaskStatus(TaskStatus.AddedToQueue);
            serializer.persist(concurrentTaskQueue, task);

            if (workerThreadPool.getActiveCount() < workerThreadCount) {
                workerThreadPool.execute(this);
            }
        }
    }

    public void cancelRequest(Task<?> request) {

        if (concurrentTaskQueue.removeWaiting(request)) {
            request.setTaskStatus(TaskStatus.Canceled);
        }
        CancelationToken token = cancelation.get(request);
        if (token != null) {
            token.cancel();
        }
    }

    public void cancelAll() {

        for (Map.Entry<Task<?>, CancelationToken> entry : cancelation.entrySet()) {
            entry.getValue().cancel();
            entry.getKey().setTaskStatus(TaskStatus.Canceled);
        }

//        callbacks.clear();
        concurrentTaskQueue.clear();
    }

    public <T> void registerCallbackForRequest(Task<T> request, TaskCallback<T> callback) {
        if (callback != null) {
            List<TaskCallback> callbacksList = null;
            if (callbacks.containsKey(request)) {
                callbacksList = callbacks.get(request);
            } else {
                callbacksList = new ArrayList<>(1);
                callbacks.put(request, callbacksList);
            }
            if (!callbacksList.contains(callback)) {
                callbacksList.add(callback);
            }
        }
    }

    public <T> void unregisterCallbackForRequest(Task<T> request, TaskCallback<T> callback) {
        List<TaskCallback> listOfcallback = callbacks.get(request);
        if (listOfcallback != null) {
            listOfcallback.remove(callback);
        }
    }

    public <T> TaskStatus getTaskStatus(Task<T> request) {

        Task taskCached = tasks.get(request.getId());
        if (taskCached == null) {
            tasks.put(request.getId(), request);
            return TaskStatus.NotExistsInQueue;
        } else {
            taskCached.setIsAttached();
        }
        return taskCached.getTaskStatus();
    }

    public Task findTaskById(String id) {
        return tasks.get(id);
    }


    public synchronized boolean tasksLoaded() {
        return this.tasksLoadedFlag;
    }

    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public Map<String, Task<?>> getTasks() {
        return tasks;
    }

    @Override
    public void setConcurrentTaskQueue(TaskQueue concurrentTaskQueue) {
        this.concurrentTaskQueue = concurrentTaskQueue;
    }

    @Override
    public void setTasksLoadedFlag(boolean b) {
        tasksLoadedFlag = b;
    }

    @Override
    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    @Override
    public void setWorkerThreadPool(ThreadPoolExecutor workerThreadPool) {
        this.workerThreadPool = workerThreadPool;
    }

    public void configure(TaskManagerConfiguration configuration) {
        this.injector = configuration.getInjector();
        this.workerThreadCount = configuration.getMaxWorkerCount();
        this.logger = configuration.getLogging();
    }

    public void shutdown() {
        workerThreadPool.shutdown();
    }
}
