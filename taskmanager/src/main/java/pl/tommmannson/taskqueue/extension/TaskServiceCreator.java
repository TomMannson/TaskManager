package pl.tommmannson.taskqueue.extension;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import pl.tommmannson.taskqueue.JobInvokation;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskThread;

/**
 * Created by tomasz.krol on 2018-02-08.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TaskServiceCreator extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {

        TaskManager manager = TaskManager.getInstance(job.getExtras().getInt("MANAGER_ID"));
        TaskThread tasks = (TaskThread) manager.getService();

        JobInvokation invocation = tasks.createNew(job, this);
        invocation.invoke();
        job.getStop
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        TaskManager manager = TaskManager.getInstance(1);
        TaskThread tasks = (TaskThread) manager.getService();
        JobInvokation invocation = tasks.createNew(job, this);
        return invocation.invokeError();//job.shouldReplaceCurrent() && tasks
    }
}
