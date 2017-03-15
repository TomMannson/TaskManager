package pl.tommmannson.taskqueue.persistence;


import android.content.res.Configuration;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.queues.TaskQueue;

/**
 * Created by tomasz.krol on 2016-10-24.
 */

public interface Serializer {

    void persist(TaskQueue queue, Task taskToPersist);

    void restore(TaskQueue queue);

    void configure(Configuration config);
}
