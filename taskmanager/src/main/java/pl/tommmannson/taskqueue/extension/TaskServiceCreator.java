package pl.tommmannson.taskqueue.extension;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import pl.tommmannson.taskqueue.JobInvokation;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskThread;

/**
 * Created by tomasz.krol on 2018-02-08.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TaskServiceCreator extends JobService implements Handler.Callback {

    @Override
    public boolean onStartJob(final JobParameters job) {

        final TaskManager manager = TaskManager.getInstance(job.getExtras().getInt("MANAGER_ID"));
        TaskThread tasks = (TaskThread) manager.getService();

        final Handler handler = new Handler(Looper.getMainLooper(), this);

        if (tasks == null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    TaskThread tasks = (TaskThread) manager.getService();
                    while (tasks == null) {
                        Log.d("TASK_QUEUE", "not initilised");
                        Thread.yield();
                        tasks = (TaskThread) manager.getService();
                    }
                    Log.d("TASK_QUEUE", "initilised");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JobInvokation invocation = tasks.createNew(job, TaskServiceCreator.this);
                    invocation.invoke();
                    Log.d("TASK_QUEUE", "task_started");

                }
            });
            t.start();
            ContextWrapper wraper = new ContextWrapper(this);
        } else {
            JobInvokation invocation = tasks.createNew(job, this);
            invocation.invoke();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        TaskManager manager = TaskManager.getInstance(job.getExtras().getInt("MANAGER_ID"));
        TaskThread tasks = (TaskThread) manager.getService();
        JobInvokation invocation = tasks.createNew(job, this);
        return invocation.invokeError();//job.shouldReplaceCurrent() && tasks
    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;
    }
}
