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
import pl.tommmannson.taskqueue.persistence.RetryOperation;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.persistence.serialization.FileSerializer;
import pl.tommmannson.taskqueue.persistence.sqlite.SqlSerializer;
import pl.tommmannson.taskqueue.progress.ProgressManager;
import pl.tommmannson.taskqueue.progress.ProgressManagerFactory;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.queues.TaskQueue;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class TaskQueueThread implements Bootable, TaskManagementInterface {

    static public final Class<?> TAG = TaskQueueThread.class;
    private final Context ctx;

    private Map<String, List<TaskCallback>> callbacks = new HashMap<>();
    final private Map<Task<?>, CancelationToken> cancelation = new HashMap<>();
    final private Map<String, Task<?>> tasks = new HashMap<>();
    private TaskQueue concurrentTaskQueue = null;
    private Serializer serializer;

    int workerThreadCount;
    DependencyInjector injector = null;
    ThreadPoolExecutor workerThreadPool = null;
    private boolean tasksLoadedFlag;
    private boolean logger;
    private int queueId;
    private int serialisationType;

    public TaskQueueThread(Context ctx){
        this.ctx = ctx;
    }

    public void start() {
        Bootstraper.start(this);
    }

    @Override
    public void setQueueId(int queueId) {
        this.queueId = queueId;
        if(serialisationType == TaskManagerConfiguration.FILE_SERIALIZABLE){
            serializer = new FileSerializer(ctx, queueId);
        }
        else{
            serializer = new SqlSerializer(ctx.getApplicationContext(), queueId);
        }
    }

    @Override
    public void run() {

        if (concurrentTaskQueue.hasTasksWaitingForExecution()) {
            Task request = null;
            try {

                request = concurrentTaskQueue.getTaskFromQueue();

                if (request == null) {
                    return;
                }

                performTaskWork(request);

                concurrentTaskQueue.removeProcessing(request);

            } catch (InterruptedException ex) {
                concurrentTaskQueue.moveFromExecutingToWaiting(request);
            } catch (CancelationException ex) {
                Log.d(this.getClass().getName(), String.format("%s canceled", request));

                ProgressManager manager = ProgressManagerFactory.create(request.getId(), callbacks);
                manager.postResult(request);

                if (request != null) {
                    cancelation.remove(request);
                    concurrentTaskQueue.removeProcessing(request);
                    request.setExecutionStatus(TaskStatus.Canceled);
                }
            } catch (Exception ex) {

                Log.e("TaskService", "Exception during request process on  ");
                ex.printStackTrace();

                if (request != null) {
                    if (request.nextRetry()) {
                        //concurrentTaskQueue.moveFromExecutingToWaiting(request);
                        concurrentTaskQueue.moveToPending(request, new RetryOperation() {
                            @Override
                            public void doOnRetry() {
                                workerThreadPool.execute(TaskQueueThread.this);
                            }
                        });
                        workerThreadPool.execute(this);
                    } else {
                        concurrentTaskQueue.removeProcessing(request);
                    }

                    request.setExecutionStatus(TaskStatus.FailFinished);
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
        ProgressManager manager = ProgressManagerFactory.create(request.getId(), callbacks);

        request.setExecutionStatus(TaskStatus.InProgress);

        if (injector != null) {
            injector.inject(request);
        }
        request.attachProgressManager(manager);
        request.doWork(token);
        if(request.getState().getException() != null){
            throw request.getState().getException();
        }
        cancelation.remove(request);

        request.setExecutionStatus(TaskStatus.SuccessfullyFinished);
    }

    public void addRequest(Task<?> task) {

        if (concurrentTaskQueue.pushToQueue(task)) {
            tasks.put(task.getId(), task);
            task.setExecutionStatus(TaskStatus.AddedToQueue);
            serializer.persist(concurrentTaskQueue, task);

            if (workerThreadPool.getActiveCount() < workerThreadCount) {
                workerThreadPool.execute(this);
            }
        }
    }

    public void cancelRequest(Task<?> request) {

        if (concurrentTaskQueue.removeWaiting(request)) {
            request.setExecutionStatus(TaskStatus.Canceled);
        }
        CancelationToken token = cancelation.get(request);
        if (token != null) {
            token.cancel();
        }
    }

    public void cancelAll() {

        for (Map.Entry<Task<?>, CancelationToken> entry : cancelation.entrySet()) {
            entry.getValue().cancel();
            entry.getKey().setExecutionStatus(TaskStatus.Canceled);
        }

//        callbacks.clear();
        concurrentTaskQueue.clear();
    }

    public <T> void registerCallbackForRequest(String requestId, TaskCallback callback) {
        if (callback != null) {
            List<TaskCallback> callbacksList = null;
            if (callbacks.containsKey(requestId)) {
                callbacksList = callbacks.get(requestId);
            } else {
                callbacksList = new ArrayList<>(1);
                callbacks.put(requestId, callbacksList);
            }
            if (!callbacksList.contains(callback)) {
                callbacksList.add(callback);
            }
        }
    }

    public <T> void unregisterCallbackForRequest(String requestId, TaskCallback callback) {
        List<TaskCallback> listOfcallback = callbacks.get(requestId);
        if (listOfcallback != null) {
            listOfcallback.remove(callback);
        }
    }

    public Task findTaskById(String id) {
        return tasks.get(id);
    }


    public synchronized boolean tasksLoaded() {
        return this.tasksLoadedFlag;
    }

    @Override
    public void addTaskToTracking(Task task) {
        tasks.put(task.getId(), task);
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
        this.serialisationType = configuration.getTaskMethodSerialisation();
    }

    public void shutdown() {
        workerThreadPool.shutdown();
    }
}
