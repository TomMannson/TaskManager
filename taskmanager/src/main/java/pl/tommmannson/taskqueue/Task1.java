package pl.tommmannson.taskqueue;

import java.io.Serializable;

import pl.tommmannson.taskqueue.persistence.Serializer;

/**
 * Created by tomasz.krol on 2017-05-25.
 */

public abstract class Task1<T extends Serializable> extends Task<T, Void> {

    public Task1(TaskParams params) {
        super(params);
    }
}
