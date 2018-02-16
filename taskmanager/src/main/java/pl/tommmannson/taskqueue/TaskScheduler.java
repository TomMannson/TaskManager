package pl.tommmannson.taskqueue;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.io.Serializable;

import pl.tommmannson.taskqueue.config.DefaultTaskSchedulerConfiguration;
import pl.tommmannson.taskqueue.extension.TaskServiceCreator;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TaskScheduler {

    public static final Class<?> TAG = TaskScheduler.class;

    TaskManager manager = null;

    private TaskScheduler(TaskManager manager) {
        this.manager = manager;
    }

    public void registerMessageQueueReady(OnManagerReadyListener listener) {
        manager.registerMessageQueueReady(listener);
    }

    public void unregisterMessageQueueReady(OnManagerReadyListener listener) {
        manager.unregisterMessageQueueReady(listener);
    }

    public <T extends Task> TaskBuilder<T> build(Class<T> clazz) {
        return new TaskBuilder<>(clazz).manager(this.manager);
    }

    public void start(Context context) {
        manager.start(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public <T extends Serializable> void scheduleTask(final Task<T, ?> task, JobInfo.Builder job) {

        manager.doTask(task, job);
    }

    public <T extends Serializable, Progress> void remove(Task<T, Progress> tProgressTask) {
    }

    public static void createInstance(Context ctx) {

        TaskManager.createInstance(-150, new DefaultTaskSchedulerConfiguration(ctx));
    }

    public static TaskScheduler getInstance() {

        TaskManager manager = TaskManager.getInstance(-150);
        return new TaskScheduler(manager);
    }

    public static <T extends Task> JobInfo.Builder buildJob(int jobId)  {

        TaskManager manager = TaskManager.getInstance(-150);

        JobInfo.Builder builder =
                new JobInfo.Builder(jobId, new ComponentName(manager.configuration.getContext(), TaskServiceCreator.class));
        builder.setMinimumLatency(0);
        return builder;
    }
}
