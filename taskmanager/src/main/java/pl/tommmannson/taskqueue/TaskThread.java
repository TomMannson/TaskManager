package pl.tommmannson.taskqueue;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import pl.tommmannson.taskqueue.bootstraping.Bootable;
import pl.tommmannson.taskqueue.bootstraping.Bootstraper;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.config.di.DependencyInjector;
import pl.tommmannson.taskqueue.extension.TaskServiceCreator;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.persistence.serialization.FileSerializer;
import pl.tommmannson.taskqueue.persistence.sqlite.SqlSerializer;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.queues.TaskQueue;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TaskThread implements Bootable, TaskManagementInterface {

    static public final Class<?> TAG = TaskThread.class;
    private final Context ctx;

    private Map<String, List<TaskCallback>> callbacks = new HashMap<>();
    final private Map<Task<?, ?>, CancelationToken> cancelation = new HashMap<>();
    final private Map<String, Task<?, ?>> tasks = new HashMap<>();
    //    private TaskQueue concurrentTaskQueue = null;
    private Serializer serializer;
    private JobScheduler dispather;

    int workerThreadCount;
    DependencyInjector injector = null;
    ThreadPoolExecutor workerThreadPool = null;
    private boolean tasksLoadedFlag;
    private int serialisationType;

    public TaskThread(Context ctx) {
        this.ctx = ctx;
        this.dispather = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public void start() {
        Bootstraper.start(this);
    }

    @Override
    public void setQueueId(int queueId) {
        if (serialisationType == TaskManagerConfiguration.FILE_SERIALIZABLE) {
            serializer = new FileSerializer(ctx, queueId);
        } else {
            serializer = new SqlSerializer(ctx.getApplicationContext(), queueId);
        }
    }

    @Override
    public void run() {

    }

    public void addRequest(Task<?, ?> task) {

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("TAG", task.getId());
        bundle.putInt("MANAGER_ID", task.getManagerId());

        JobInfo job = new JobInfo.Builder(task.getId().hashCode(), new ComponentName(ctx, TaskServiceCreator.class))
                .setExtras(bundle)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)
                .setOverrideDeadline(10000)
//                .setTag()
//                .setService(TaskServiceCreator.class)
//                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
//                .setTrigger(Trigger.executionWindow(0, 10))
//                .setReplaceCurrent(true)
                .build();

        dispather.schedule(job);
        tasks.put(task.getId(), task);
        task.setExecutionStatus(TaskStatus.AddedToQueue);
        serializer.persist(null, task);
    }

    public void cancelRequest(Task<?, ?> request) {

        CancelationToken token = cancelation.get(request);
        if (token != null) {
            token.cancel();
        }

        dispather.cancel(request.getId().hashCode());
        request.setExecutionStatus(TaskStatus.Canceled);
    }

    public void cancelAll() {

        for (Map.Entry<Task<?, ?>, CancelationToken> entry : cancelation.entrySet()) {
            entry.getValue().cancel();
            entry.getKey().setExecutionStatus(TaskStatus.Canceled);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dispather.cancelAll();
        }
    }

    public void registerCallbackForRequest(String requestId, TaskCallback callback) {
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

    public void unregisterCallbackForRequest(String requestId, TaskCallback callback) {
        List<TaskCallback> listOfcallback = callbacks.get(requestId);
        if (listOfcallback != null) {
            listOfcallback.remove(callback);
        }
    }

    public Task findTaskById(String id) {
        return tasks.get(id);
    }

    public Task[] findByIds(String... ids) {

        Task[] tasksFound = new Task[ids.length];

        for (int i = 0; i < ids.length; i++) {
            tasksFound[i] = tasks.get(ids[i]);
        }
        return tasksFound;
    }


    public synchronized boolean tasksLoaded() {
        return this.tasksLoadedFlag;
    }

    //    @Override
    public void addTaskToTracking(Task task) {
        tasks.put(task.getId(), task);
    }

    //    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    //    @Override
    public Map<String, Task<?, ?>> getTasks() {
        return tasks;
    }

    //    @Override
    public void setConcurrentTaskQueue(TaskQueue concurrentTaskQueue) {
//        this.concurrentTaskQueue = concurrentTaskQueue;
    }

    //    @Override
    public void setTasksLoadedFlag(boolean b) {
        tasksLoadedFlag = b;
    }

    //    @Override
    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    //    @Override
    public void setWorkerThreadPool(ThreadPoolExecutor workerThreadPool) {
        this.workerThreadPool = workerThreadPool;
    }

    public void configure(TaskManagerConfiguration configuration) {
        this.injector = configuration.getInjector();
        this.workerThreadCount = configuration.getMaxWorkerCount();
//        this.logger = configuration.getLogging();
        this.serialisationType = configuration.getTaskMethodSerialisation();
    }

    public void shutdown() {
        workerThreadPool.shutdown();
    }

    public JobInvokation createNew(JobParameters job, JobService service) {
        JobInvokation invokation = new JobInvokation(job, service);
        invokation.init(tasks, callbacks, cancelation, injector, serializer);
        return invokation;
    }
}
