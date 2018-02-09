package pl.tommmannson.taskqueue.progress;

import java.io.Serializable;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.persistence.Serializer;

/**
 * Created by tomasz.krol on 2015-11-13.
 */
public interface ResultCallback<Result extends Serializable> {

    void onResult(Task<Result, ?> task);
}
