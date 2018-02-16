package pl.tommmannson.taskqueue.config;


import android.content.Context;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class DefaultTaskSchedulerConfiguration extends TaskManagerConfiguration {
    public DefaultTaskSchedulerConfiguration(Context ctx) {
        super(null, null, 3, 1, true, true,ctx);
    }
}
