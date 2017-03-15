package pl.tommmannson.taskqueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.messaging.MessageDispather;
import pl.tommmannson.taskqueue.messaging.MessageFactory;
import pl.tommmannson.taskqueue.messaging.impl.AddTaskMessage;
import pl.tommmannson.taskqueue.messaging.impl.BootServiceMessage;
import pl.tommmannson.taskqueue.messaging.impl.CancelTaskMessage;
import pl.tommmannson.taskqueue.messaging.impl.RegisterCallbackMessage;
import pl.tommmannson.taskqueue.messaging.impl.UnregisterCallbackMessage;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class TaskManager {

    public static final Class<?> TAG = TaskManager.class;
    static Map<Integer, TaskManager> instances = new HashMap<>();
    public static TaskManager DEFAULT;

    TaskManagerConfiguration configuration;
    TaskManagementInterface service;
    MessageDispather messageDispatcher;
    MessageFactory factory = new MessageFactory();
    List<Task> statusToUpdate = new ArrayList<>();
    private int id;

    public TaskManager(TaskManagerConfiguration config) {
        messageDispatcher = new MessageDispather(factory);

        if (config == null) {
            throw new NullPointerException("configuration can't be null");
        }

        configuration = config;
        if (DEFAULT == null) {
            DEFAULT = this;
        }
    }

    public void start(Context context) {
        BootServiceMessage message = factory.obtain(BootServiceMessage.class);
        message.setAppContext(context);
        message.setConfig(configuration);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void doTask(final Task<T> task) {

        if (task == null)
            return;

        AddTaskMessage message = factory.obtain(AddTaskMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void registerCallback(final Task<T> task, final TaskCallback callback) {

        if (task == null || callback == null)
            return;

        RegisterCallbackMessage message = factory.obtain(RegisterCallbackMessage.class);
        message.setTask(task);
        message.setTaskCallback(callback);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void unregisterCallback(final Task<T> task, final TaskCallback callback) {

        if (task == null || callback == null)
            return;

        UnregisterCallbackMessage message = factory.obtain(UnregisterCallbackMessage.class);
        message.setTask(task);
        message.setTaskCallback(callback);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void cancelRequest(final Task<T> task) {

        if (task == null)
            return;

        CancelTaskMessage message = factory.obtain(CancelTaskMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public void cancelAllRequests() {
        if (service != null) {
            service.cancelAll();
        }
    }

    public <T> TaskStatus checkExecutionStatus(final Task<T> request) {
        if (service == null) {
            statusToUpdate.add(request);
            return TaskStatus.NotExistsInQueue;
        } else {
            return service.getTaskStatus(request);
        }
    }

    public <T extends Task> TaskContainer<T> findTaskById(String id) {
        T task = null;
        TaskContainer<T> container = new TaskContainer<>();
        if (service != null) {
            task = (T) service.findTaskById(id);
            container.setTask(task);
            container.setReady(true);
        } else {
            container.setIdForQuery(id);
            container.setReady(false);
        }
        return container;
    }

    public TaskManagementInterface getService() {
        return service;
    }

    public void setService(TaskQueueThread service) {
        this.service = service;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public class RequestServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if (service instanceof TaskService.RequestBinder) {
                TaskService.RequestBinder binder = (TaskService.RequestBinder) service;


                TaskManager.this.service = binder.getService();
                TaskManager.this.service.configure(configuration);
                ((TaskService.RequestBinder) service).service.setQueueId(id);

                TaskManager.this.service.start();
                for (Task task : statusToUpdate) {
                    checkExecutionStatus(task);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public static TaskManager getInstance(int id, TaskManagerConfiguration config) {

        TaskManager manager = instances.get(id);

        if (manager == null) {
            manager = new TaskManager(config);
            manager.setId(id);
            instances.put(id, manager);
        }

        return manager;
    }
}
