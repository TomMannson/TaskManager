package pl.tommmannson.taskqueue;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import pl.tommmannson.taskqueue.bootstraping.Bootstraper;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.di.DependencyInjector;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.persistence.sqlite.SqlSerializer;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.queues.TaskQueue;


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
    public void addRequest(Task<?> task) {

        taskQueueThread.addRequest(task);
    }

    @Override
    public void cancelRequest(Task<?> request) {

        taskQueueThread.cancelRequest(request);
    }

    @Override
    public void cancelAll() {

        taskQueueThread.cancelAll();
    }

    @Override
    public <T> void registerCallbackForRequest(Task<T> request, TaskCallback callback) {

        taskQueueThread.registerCallbackForRequest(request, callback);
    }

    @Override
    public <T> void unregisterCallbackForRequest(Task<T> request, TaskCallback callback) {

        taskQueueThread.unregisterCallbackForRequest(request, callback);
    }

    @Override
    public <T> TaskStatus getTaskStatus(Task<T> request) {

        return taskQueueThread.getTaskStatus(request);
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