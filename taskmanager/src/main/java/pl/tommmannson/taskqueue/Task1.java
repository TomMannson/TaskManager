package pl.tommmannson.taskqueue;

/**
 * Created by tomasz.krol on 2017-05-25.
 */

public abstract class Task1<T> extends Task<T, Void> {

    protected Task1(TaskParams params) {
        super(params);
    }
}
