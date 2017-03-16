package pl.tommmannson.taskqueue;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by tomasz.krol on 2016-11-04.
 */
public class TaskContainer<T extends Task> {

    private T task;
    private boolean ready;
    private String idForQuery;
    private int managerId;

    public T getTask() {
        return task;
    }

    public void setTask(T task) {
        this.task = task;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }



    public void executeAsync(final TaskContainerCallback listener){
        Thread t = new Thread(){
            @Override
            public void run() {
                while (TaskManager.DEFAULT.service == null || !TaskManager.DEFAULT.service.tasksLoaded()){
                    Thread.yield();
                }

                final Task task = TaskManager.DEFAULT.service.findTaskById(idForQuery);
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onTaskLoaded(idForQuery, task);
                    }
                });
            }
        };
        t.start();
    }

    public void setIdForQuery(String idForQuery) {
        this.idForQuery = idForQuery;
    }

    public String getIdForQuery() {
        return idForQuery;
    }

    public boolean isReady() {
        return ready;
    }

    public interface TaskContainerCallback{
        void onTaskLoaded(String taskId, Task task);
    }
}
