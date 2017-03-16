package pl.tommmannson.taskqueue.config;


import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import pl.tommmannson.taskqueue.TaskService;
import pl.tommmannson.taskqueue.di.DependencyInjector;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static pl.tommmannson.taskqueue.config.TaskManagerConfiguration.FILE_SERIALIZABLE;
import static pl.tommmannson.taskqueue.config.TaskManagerConfiguration.SQLITE_SERIALIZABLE;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class TaskManagerConfiguration {

    private final Context ctx;
    Class<? extends TaskService> classOfService;

    DependencyInjector injector;

    int maxWorkerCount;
    int taskMethodSerialisation;
    boolean threadPoolMode;
    private boolean logging;

    TaskManagerConfiguration(Class<? extends TaskService> classOfService, DependencyInjector injector, int maxWorkerCount,
                             int taskMethodSerialisation, Context ctx) {
        this(classOfService, injector, maxWorkerCount, taskMethodSerialisation, false, ctx);
    }

    TaskManagerConfiguration(Class<? extends TaskService> classOfService, DependencyInjector injector, int maxWorkerCount,
                             int taskMethodSerialisation, boolean threadPoolMode, Context ctx) {
        this.classOfService = classOfService;
        this.injector = injector;
        this.maxWorkerCount = maxWorkerCount;
        this.taskMethodSerialisation = taskMethodSerialisation;
        this.threadPoolMode = threadPoolMode;
        this.ctx = ctx;
    }

    public Class<? extends TaskService> getClassOfService() {
        return classOfService;
    }

    public DependencyInjector getInjector() {
        return injector;
    }

    public int getMaxWorkerCount() {
        return maxWorkerCount;
    }

    public boolean isThreadPoolMode() {
        return threadPoolMode;
    }

    public boolean getLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public int getTaskMethodSerialisation() {
        return taskMethodSerialisation;
    }

    public Context getContext() {
        return ctx;
    }

    public static class Builder {

        private Class<? extends TaskService> classOfService;
        private DependencyInjector injector = null;
        private int maxWorkerCount = 3;
        private int taskMethodSerialisation;
        private boolean threadPoolMode;
        private Context ctx;

        public Builder(Context ctx) {
            this.ctx = ctx;
        }

        public Builder setClassOfService(Class<? extends TaskService> classOfService) {
            this.classOfService = classOfService;
            threadPoolMode = false;
            return this;
        }

        public Builder setInjector(DependencyInjector injector) {
            this.injector = injector;
            return this;
        }

        public Builder setTaskMethodSerialisation(@NavigationMode int taskMethodSerialisation) {
            this.taskMethodSerialisation = taskMethodSerialisation;
            return this;
        }

        public Builder setMaxWorkerCount(int maxWorkerCount) {
            this.maxWorkerCount = maxWorkerCount;
            return this;
        }

        public Builder setQueueExecutionMode(@QueueMode int mode) {
            this.threadPoolMode = mode == THREAD_POOL_QUEUE;
            return this;
        }

        public TaskManagerConfiguration build() {
            if(maxWorkerCount <= 0){
                throw new IllegalStateException("maxWorkerCount should be greather then 0");
            }

            return new TaskManagerConfiguration(classOfService, injector, maxWorkerCount, taskMethodSerialisation,
                    threadPoolMode, ctx);
        }
    }

    @Retention(SOURCE)
    @IntDef({FILE_SERIALIZABLE, SQLITE_SERIALIZABLE})
    public @interface NavigationMode {}
    public static final int FILE_SERIALIZABLE = 0;
    public static final int SQLITE_SERIALIZABLE = 1;

    @Retention(SOURCE)
    @IntDef({SERVICE_QUEUE, THREAD_POOL_QUEUE})
    public @interface QueueMode {}
    public static final int SERVICE_QUEUE = 0;
    public static final int THREAD_POOL_QUEUE = 1;
}
