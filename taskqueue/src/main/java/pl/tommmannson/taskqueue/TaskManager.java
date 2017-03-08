package pl.tommmannson.taskqueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class TaskManager /*implements Runnable*/ {

    public static final Class<?> TAG = TaskManager.class;
    public static TaskManager DEFAULT;


    TaskManagerConfiguration configuration = null;
    ExecutorService asyncRequestAdd = Executors.newSingleThreadExecutor();
    WeakReference<Context> weakContext;
    RequestServiceConnection connection = null;
    TaskManagementInterface service = null;
    MessageDispather messageDispatcher = new MessageDispather();
    MessageFactory factory = new MessageFactory();

    private boolean startingInProgress;
    private boolean isBinded = false;

    public TaskManager(TaskManagerConfiguration config){

        if(config == null){
            throw new NullPointerException("configuration can't be null");
        }

        configuration = config;
        DEFAULT = this;
    }

    public void start(Context context) {
        BootServiceMessage message = factory.obtain(BootServiceMessage.class);
        message.setAppContext(context);
        message.setConfig(configuration);
        message.setManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void doTask(final Task<T> task) {

        if(task == null)
            return;

        AddTaskMessage message = factory.obtain(AddTaskMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);
    }

    public <T> void registerCallback(final Task<T> task, final TaskCallback<T> callback) {

        if(task == null || callback == null)
            return;

//        if(service != null && !startingInProgress){
//            service.registerCallbackForRequest(task, callback);
//            return;
//        }

        RegisterCallbackMessage message = factory.obtain(RegisterCallbackMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);

//        asyncRequestAdd.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (service == null || startingInProgress) {
//                    Thread.yield();
//                }
//                if (service != null) {
//                    service.registerCallbackForRequest(task, callback);
//                }
//            }
//        });
    }

    public <T> void unregisterCallback(final Task<T> task, final TaskCallback<T> callback) {

        if(task == null || callback == null)
            return;

//        if(service != null && !startingInProgress){
//            service.unregisterCallbackForRequest(task, callback);
//            return;
//        }
//
//        asyncRequestAdd.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (service == null || startingInProgress) {
//                    Thread.yield();
//                }
//                if (service != null) {
//                    service.unregisterCallbackForRequest(task, callback);
//                }
//            }
//        });

        UnregisterCallbackMessage message = factory.obtain(UnregisterCallbackMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);

    }

    public <T> void cancelRequest(final Task<T> task) {

        if(task == null)
            return;

        CancelTaskMessage message = factory.obtain(CancelTaskMessage.class);
        message.setTask(task);
        message.setTaskManager(this);
        messageDispatcher.dispatch(message);

        asyncRequestAdd.execute(new Runnable() {
            @Override
            public void run() {
                while (service == null || startingInProgress) {
                    Thread.yield();
                }
                if (service != null) {
                    service.cancelRequest(task);
                }
            }
        });
    }

    public void cancelAllRequests() {
        if(service != null){
            service.cancelAll();
        }
    }

    public <T> TaskStatus checkExecutionStatus(final Task<T> request) {
        if (service == null || startingInProgress){
            return TaskStatus.NotExistsInQueue;
        }
        else{
            return service.getTaskStatus(request);
        }
    }

    private Context getContext() {
        return weakContext.get();
    }

    <T extends Task> TaskContainer<T> findTaskById(String id) {
        T task = null;
        TaskContainer<T> container = new TaskContainer<>();
        if(service != null){
            task = (T) service.findTaskById(id);
            container.setTask(task);
            container.setReady(true);
        }
        else{
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

    public class RequestServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBinded = true;
            if (service instanceof TaskService.RequestBinder) {
                TaskService.RequestBinder binder = (TaskService.RequestBinder) service;
                TaskManager.this.service = binder.getService();

                TaskManager.this.service.configure(configuration);
                TaskManager.this.service.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinded = false;
        }
    }
}
