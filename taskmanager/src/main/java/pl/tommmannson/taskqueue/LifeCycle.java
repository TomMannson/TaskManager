package pl.tommmannson.taskqueue;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import pl.tommmannson.taskqueue.progress.ErrorCallback;
import pl.tommmannson.taskqueue.progress.ResultCallback;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2017-11-27.
 */

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public interface LifeCycle {

    <RESULT>void observeTaskP(Task<RESULT, ?> task,
                                  ResultCallback<RESULT> result
    );

    <RESULT>void observeTaskP(Task<RESULT, ?> task,
                              ResultCallback<RESULT> result, ErrorCallback error
    );

    <RESULT, PROGRESS>void observeTask(Task<RESULT, PROGRESS> task,
                                            TaskCallback<RESULT, PROGRESS> result
    );

    <RESULT> void stopObserveTaskP(Task<RESULT, ?> task, ResultCallback<RESULT> result);

    <RESULT>void stopObserveTaskP(Task<RESULT, ?> task,
                              ResultCallback<RESULT> result, ErrorCallback error
    );

    <RESULT, PROGRESS> void stopObserveTask(Task<RESULT, PROGRESS> task, TaskCallback<RESULT, PROGRESS> result);
}
