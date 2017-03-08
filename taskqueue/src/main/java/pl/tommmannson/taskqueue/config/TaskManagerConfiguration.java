package pl.tommmannson.taskqueue.config;


import android.content.Context;

import pl.tommmannson.taskqueue.TaskService;
import pl.tommmannson.taskqueue.di.DependencyInjector;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class TaskManagerConfiguration {

    Class<? extends TaskService> classOfService;

    DependencyInjector injector;

    int maxWorkerCount;
    int taskMethodSerialisation;
    boolean threadPoolMode;
    private boolean logging;

    TaskManagerConfiguration(Class<? extends TaskService> classOfService, DependencyInjector injector, int maxWorkerCount,
                             int taskMethodSerialisation) {
        this(classOfService, injector, maxWorkerCount, taskMethodSerialisation, false);
    }

    TaskManagerConfiguration(Class<? extends TaskService> classOfService, DependencyInjector injector, int maxWorkerCount,
                             int taskMethodSerialisation, boolean threadPoolMode) {
        this.classOfService = classOfService;
        this.injector = injector;
        this.maxWorkerCount = maxWorkerCount;
        this.taskMethodSerialisation = taskMethodSerialisation;
        this.threadPoolMode = threadPoolMode;
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

    public static class Builder {
        private Class<? extends TaskService> classOfService;
        private DependencyInjector injector = null;
        private int maxWorkerCount = 3;
        private int taskMethodSerialisation;
        private boolean threadPoolMode;

        public Builder setClassOfService(Class<? extends TaskService> classOfService) {
            this.classOfService = classOfService;
            threadPoolMode = false;
            return this;
        }

        public Builder setInjector(DependencyInjector injector) {
            this.injector = injector;
            return this;
        }

        public Builder setTaskMethodSerialisation(int taskMethodSerialisation) {
            this.taskMethodSerialisation = taskMethodSerialisation;
            return this;
        }

        public Builder setMaxWorkerCount(int maxWorkerCount) {
            this.maxWorkerCount = maxWorkerCount;
            return this;
        }

        public Builder setThreadPoolMode(Context ctx) {
            this.threadPoolMode = true;
            return this;
        }

        public Builder setServiceMode(Context ctx) {
            this.threadPoolMode = false;
            return this;
        }

        public TaskManagerConfiguration build() {
            if(maxWorkerCount <= 0){
                throw new IllegalStateException("maxWorkerCount should be greather then 0");
            }

            return new TaskManagerConfiguration(classOfService, injector, maxWorkerCount, taskMethodSerialisation,
                    threadPoolMode);
        }
    }
}
