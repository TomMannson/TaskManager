package pl.tommmannson.taskqueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

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

    public TaskQuery findTaskById(String id) {
        TaskQuery container = new TaskQuery();
        container.setIdForQuery(new String[]{id});
        container.setManager(this);
        return container;
    }

    public TaskQuery findTaskById(String[] ids) {
        TaskQuery container = new TaskQuery();
        container.setIdForQuery(ids);
        container.setManager(this);
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

                TaskManagementInterface bindedService = binder.getService();
                bindedService.configure(configuration);
                bindedService.setQueueId(id);
                bindedService.start();

                TaskManager.this.service = bindedService;
                for (Task task : statusToUpdate) {
                    checkExecutionStatus(task);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public static TaskManager createInstance(int id, TaskManagerConfiguration config) {

        TaskManager manager = instances.get(id);

        if (manager == null) {
            manager = new TaskManager(config);
            manager.setId(id);
            instances.put(id, manager);
        }

        manager.start(config.getContext());
        return manager;
    }

    public static TaskManager getInstance(int id) {

        TaskManager manager = instances.get(id);

        return manager;
    }
}
