package pl.tommmannson.taskqueue.config.di;


import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public interface DependencyInjector {
    void inject(Task job);
}
