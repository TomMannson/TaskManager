package pl.tommmannson.taskqueue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import pl.tommmannson.taskqueue.bootstraping.Bootstraper;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.progress.TaskCallback;


public class TaskService extends Service implements TaskManagementInterface{

    static public final Class<?> TAG = TaskService.class;

    private RequestBinder binder;
    private TaskQueueThread taskQueueThread;

    public TaskService() {
        binder = new RequestBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        taskQueueThread = new TaskQueueThread(getApplication());
    }

    public void start() {
        Bootstraper.start(taskQueueThread);
    }

    @Override
    public void setQueueId(int queueId) {
        taskQueueThread.setQueueId(queueId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskQueueThread.shutdown();
        binder = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void addRequest(Task<?, ?> task) {

        taskQueueThread.addRequest(task);
    }

    @Override
    public void cancelRequest(Task<?, ?> request) {

        taskQueueThread.cancelRequest(request);
    }

    @Override
    public void cancelAll() {

        taskQueueThread.cancelAll();
    }

    @Override
    public <T> void registerCallbackForRequest(String taskId, TaskCallback callback) {

        taskQueueThread.registerCallbackForRequest(taskId, callback);
    }

    @Override
    public <T> void unregisterCallbackForRequest(String taskId, TaskCallback callback) {

        taskQueueThread.unregisterCallbackForRequest(taskId, callback);
    }

    @Override
    public Task findTaskById(String id) {
        return taskQueueThread.findTaskById(id);
    }

    @Override
    public void configure(TaskManagerConfiguration configuration) {
        taskQueueThread.configure(configuration);
    }

    public synchronized boolean tasksLoaded() {
        return taskQueueThread.tasksLoaded();
    }

    @Override
    public void addTaskToTracking(Task task) {
        taskQueueThread.addTaskToTracking(task);
    }

    public static class RequestBinder extends Binder {

        TaskManagementInterface service;

        RequestBinder(TaskManagementInterface service) {
            this.service = service;
        }

        TaskManagementInterface getService() {
            return service;
        }
    }
}