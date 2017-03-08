package pl.tommmannson.taskqueue.utils;


import pl.tommmannson.taskqueue.persistence.TaskStatus;

public class TaskStatusHelper {
    public static boolean taskInProgress(TaskStatus taskStatus){
        return taskStatus == TaskStatus.AddedToQueue
                || taskStatus == TaskStatus.InProgress
                || taskStatus == TaskStatus.BeforeExecution;
    }

    public static boolean taskNotInProgress(TaskStatus taskStatus){
        return taskStatus == TaskStatus.FailFinished
                || taskStatus == TaskStatus.SuccessfullyFinished
                || taskStatus == TaskStatus.NotExistsInQueue
                || taskStatus == TaskStatus.Canceled;
    }

    public static boolean taskAfterExecution(TaskStatus taskStatus){
        return taskStatus == TaskStatus.FailFinished
                || taskStatus == TaskStatus.SuccessfullyFinished;

    }
}
