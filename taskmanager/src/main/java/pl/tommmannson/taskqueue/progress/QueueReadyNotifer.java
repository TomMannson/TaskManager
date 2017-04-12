package pl.tommmannson.taskqueue.progress;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import pl.tommmannson.taskqueue.TaskManager;

/**
 * Created by tomasz.krol on 2017-04-12.
 */

public class QueueReadyNotifer {

    List<OnManagerReadyListener> listeners = new ArrayList<>();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    boolean finished;
    TaskManager manager;

    public QueueReadyNotifer(TaskManager manager) {
        this.manager = manager;
    }

    public void addListener(OnManagerReadyListener listener){
        listeners.add(listener);
        if (finished) {
            listener.onTaskManagerReady(manager.getId());
        }
    }

    public void removeListener(OnManagerReadyListener listener){
        listeners.remove(listener);
    }

    public void notifyListeners(){
        for(int i = 0; i < listeners.size(); i++) {
            final OnManagerReadyListener listener = listeners.get(i);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskManagerReady(manager.getId());
                }
            });
        }
        finished = true;

    }

}
