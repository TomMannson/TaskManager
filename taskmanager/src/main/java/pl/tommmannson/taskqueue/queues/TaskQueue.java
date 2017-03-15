package pl.tommmannson.taskqueue.queues;


import java.util.List;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by tomasz.krol on 2016-10-24.
 */

public interface TaskQueue {

    boolean pushToQueue(Task task);

    boolean hasTasksWaitingForExecution();

    Task getTaskFromQueue() throws InterruptedException;

    boolean removeWaiting(Task task);

    boolean removeProcessing(Task task);

    void moveFromExecutingToWaiting(Task task);

    void clear();

    List<Task<?>> getFullList();

    void addFullList(List<Task<?>> listToAdd);
}
