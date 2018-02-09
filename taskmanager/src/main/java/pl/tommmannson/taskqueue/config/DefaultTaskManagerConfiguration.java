package pl.tommmannson.taskqueue.config;


import android.content.Context;

import pl.tommmannson.taskqueue.TaskService;

/**
 * Created by tomasz.krol on 2016-05-06.
 */
public class DefaultTaskManagerConfiguration extends TaskManagerConfiguration {
    public DefaultTaskManagerConfiguration(Context ctx) {
        super(null, null, 3, 1, true, true,ctx);
    }
}
