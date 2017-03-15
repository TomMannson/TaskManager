package pl.tommmannson.taskqueue.utils;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class TaskQueueReadyChecker {

    public static void waitForreadyQueue(TaskManager manager){
        TaskManagementInterface service = manager.getService();
        while (service == null) {
            Thread.yield();
            service = manager.getService();
        }
    }
}
