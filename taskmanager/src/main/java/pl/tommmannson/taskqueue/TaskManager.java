package pl.tommmannson.taskqueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.io.Serializable;
import java.util.HashMap;
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
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;
import pl.tommmannson.taskqueue.progress.QueueReadyNotifer;
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
    QueueReadyNotifer notifier = new QueueReadyNotifer(this);
    private int id;

    TaskManager(TaskManagerConfiguration config) {
        messageDispatcher = new MessageDispather(factory);

        if (config == null) {
            throw new NullPointerException("configuration can't be null");
        }

        configuration = config;
        if (DEFAULT == null) {
            DEFAULT = this;
        }
    }

    public void registerMessageQueueReady(OnManagerReadyListener listener) {
        notifier.addListener(listener);
    }

    public void unregisterMessageQueueReady(OnManagerReadyListener listener) {
        notifier.removeListener(listener);
    }

    public <T extends Task> TaskBuilder<T> build(Class<T> clazz) {
        return new TaskBuilder<T>(clazz).manager(this);
    }

    public void start(Context context) {
        BootServiceMessage message = factory.obtain(BootServiceMessage.class);
        message.setAppContext(context);
        message.setConfig(configuration);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T extends Serializable> void doTask(final Task<T, ?> task) {

        if (task == null)
            return;

        AddTaskMessage message = factory.obtain(AddTaskMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public void registerCallback(String taskId, final TaskCallback callback) {

        if (taskId == null || callback == null)
            return;

        RegisterCallbackMessage message = factory.obtain(RegisterCallbackMessage.class);
        message.setTaskId(taskId);
        message.setTaskCallback(callback);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public void unregisterCallback(String taskId, final TaskCallback callback) {

        if (taskId == null || callback == null)
            return;

        UnregisterCallbackMessage message = factory.obtain(UnregisterCallbackMessage.class);
        message.setTaskId(taskId);
        message.setTaskCallback(callback);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T extends Serializable> void cancelRequest(final Task<T, ?> task) {

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

    public TaskManagementInterface getService() {
        return service;
    }

    public void setService(TaskManagementInterface service) {
        this.service = service;
        if (service != null)
            notifier.notifyListeners();
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addTask(Task task) {
        service.addTaskToTracking(task);
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

                setService(bindedService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setService(null);
        }
    }

    public static void createInstance(int id, TaskManagerConfiguration config) {

        TaskManager manager = instances.get(id);

        if (manager == null) {
            manager = new TaskManager(config);
            manager.setId(id);
            instances.put(id, manager);
        }

        manager.start(config.getContext());
//        return manager;
    }

    public static TaskManager getInstance(int id) {

        TaskManager manager = instances.get(id);

        return manager;
    }
}
