package pl.tommmannson.taskqueue;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2017-05-25.
 */

public abstract class JobTask<T extends Serializable> extends Task<T, Void> {

    public JobTask() {

        super(new TaskParams());
    }
}
