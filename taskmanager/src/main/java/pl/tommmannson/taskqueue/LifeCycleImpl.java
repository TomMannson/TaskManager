package pl.tommmannson.taskqueue;

import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.ErrorCallback;
import pl.tommmannson.taskqueue.progress.ResultCallback;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2017-11-28.
 */

public class LifeCycleImpl implements LifeCycle {

    @Override
    public <RESULT> void observeTaskP(Task<RESULT, ?> task, ResultCallback<RESULT> result) {

        if (task.getState().getStatus() == TaskStatus.SuccessfullyFinished) {
            result.onResult(task);
        }
        task.registerPartial(result);
    }

    @Override
    public <RESULT> void observeTaskP(Task<RESULT, ?> task, ResultCallback<RESULT> result, ErrorCallback error) {

        if (task.getState().getStatus() == TaskStatus.SuccessfullyFinished) {
            result.onResult(task);
        }
        task.registerPartial(result, error);
    }

    @Override
    public <RESULT, PROGRESS> void observeTask(Task<RESULT, PROGRESS> task, TaskCallback<RESULT, PROGRESS> result) {
        if (task.getState().getStatus() == TaskStatus.SuccessfullyFinished) {
            result.onResult(task);
        } else if (task.getState().getStatus() == TaskStatus.FailFinished) {
            result.onError(task.getId(), task.getState().getException());
        }
        task.register(result);
    }

    @Override
    public <RESULT> void stopObserveTaskP(Task<RESULT, ?> task, ResultCallback<RESULT> result) {

        task.unregisterPartial(result);
    }

    @Override
    public <RESULT> void stopObserveTaskP(Task<RESULT, ?> task, ResultCallback<RESULT> result, ErrorCallback error) {

        task.registerPartial(result, error);
    }


    @Override
    public <RESULT, PROGRESS> void stopObserveTask(Task<RESULT, PROGRESS> task, TaskCallback<RESULT, PROGRESS> result) {

        task.unregister(result);
    }
}
