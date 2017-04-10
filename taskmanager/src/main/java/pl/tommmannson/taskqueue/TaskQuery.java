package pl.tommmannson.taskqueue;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomasz.krol on 2016-11-04.
 */
public class TaskQuery {

    private String[] idForQuery;
    private int managerId;

    public void executeAsync(final TaskContainerCallback listener){
        Thread t = new Thread(){
            @Override
            public void run() {
                TaskManager taskManager = TaskManager.getInstance(managerId);
                while (taskManager.service == null){
                    Thread.yield();
                }

                Task task = taskManager.service.findTaskById(idForQuery[0]);
                final Map<String, Task> result = new HashMap<>();
                result.put(idForQuery[0], task);

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onTaskLoaded(result);
                    }
                });
            }
        };
        t.start();
    }

    public void setIdForQuery(String[] idForQuery) {
        this.idForQuery = idForQuery;
    }

    public void setManager(TaskManager manager) {
        this.managerId = manager.getId();
    }

    public interface TaskContainerCallback{
        void onTaskLoaded(Map<String, Task> result);
    }
}
