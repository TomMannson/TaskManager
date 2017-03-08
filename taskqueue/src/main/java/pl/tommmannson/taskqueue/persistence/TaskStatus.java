package pl.tommmannson.taskqueue.persistence;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2015-12-21.
 */
public enum TaskStatus implements Serializable {
    NotExistsInQueue,
    AddedToQueue,
    BeforeExecution,
    InProgress,
    Canceled,
    SuccessfullyFinished,
    FailFinished,
}

