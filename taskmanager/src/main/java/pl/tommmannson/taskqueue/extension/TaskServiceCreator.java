package pl.tommmannson.taskqueue.extension;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskSchedulerThread;

/**
 * Created by tomasz.krol on 2018-02-08.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TaskServiceCreator extends JobService {

    @Override
    public boolean onStartJob(final JobParameters job) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final TaskManager manager = TaskManager.getInstance(job.getExtras().getInt("MANAGER_ID"));
                TaskSchedulerThread tasks = getTasksOrWait(manager);
                tasks.createNew(job, TaskServiceCreator.this)
                        .invoke();
            }
        });
        t.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        TaskManager manager = TaskManager.getInstance(job.getExtras().getInt("MANAGER_ID"));
        ;
        TaskSchedulerThread tasks = (TaskSchedulerThread) manager.getService();
        if (tasks == null) {
            return true;
        }
        Task task = tasks.findTaskById(job.getExtras().getString("TAG"));
        if (task == null) {
            return false;
        }

        return tasks.createNew(job, this)
                .invokeError();
    }

    @NonNull
    private TaskSchedulerThread getTasksOrWait(TaskManager manager) {
        TaskSchedulerThread tasks = (TaskSchedulerThread) manager.getService();
        while (tasks == null) {
            Thread.yield();
            tasks = (TaskSchedulerThread) manager.getService();
        }
        return tasks;
    }
}
