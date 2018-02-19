package pl.tommmannson.taskqueue.utils;


import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.TaskStatus;

@SuppressWarnings("all")
public class TaskStatusHelper {
    public static boolean taskInProgress(Task task){
        TaskStatus taskStatus = task.getState().getStatus();
        return taskStatus == TaskStatus.AddedToQueue
                || taskStatus == TaskStatus.InProgress
                || taskStatus == TaskStatus.BeforeExecution;
    }

    public static boolean taskNotInProgress(Task task){
        TaskStatus taskStatus = task.getState().getStatus();
        return taskStatus == TaskStatus.FailFinished
                || taskStatus == TaskStatus.SuccessfullyFinished
                || taskStatus == TaskStatus.NotExistsInQueue
                || taskStatus == TaskStatus.Canceled;
    }

    public static boolean taskAfterExecution(Task task){
        TaskStatus taskStatus = task.getState().getStatus();
        return taskStatus == TaskStatus.FailFinished
                || taskStatus == TaskStatus.SuccessfullyFinished;

    }
}
