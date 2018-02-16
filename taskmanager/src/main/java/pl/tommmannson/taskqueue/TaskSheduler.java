package pl.tommmannson.taskqueue;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;
import pl.tommmannson.taskqueue.config.DefaultTaskSchedulerConfiguration;
import pl.tommmannson.taskqueue.config.TaskManagerConfiguration;
import pl.tommmannson.taskqueue.config.di.DependencyInjector;
import pl.tommmannson.taskqueue.extension.TaskServiceCreator;
import pl.tommmannson.taskqueue.messaging.MessageDispather;
import pl.tommmannson.taskqueue.messaging.MessageFactory;
import pl.tommmannson.taskqueue.messaging.impl.AddTaskMessage;
import pl.tommmannson.taskqueue.messaging.impl.BootServiceMessage;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;
import pl.tommmannson.taskqueue.progress.QueueReadyNotifer;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TaskSheduler{

    public static final Class<?> TAG = TaskSheduler.class;

    TaskManager manager = null;

    private TaskSheduler(TaskManager manager) {
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

    public static TaskSheduler getInstance() {

        TaskManager manager = TaskManager.getInstance(-150);
        return new TaskSheduler(manager);
    }

    public static <T extends Task> JobInfo.Builder buildJob(int jobId)  {

        TaskManager manager = TaskManager.getInstance(-150);

        JobInfo.Builder builder =
                new JobInfo.Builder(jobId, new ComponentName(manager.configuration.getContext(), TaskServiceCreator.class));
        builder.setMinimumLatency(0);
        return builder;
    }
}
