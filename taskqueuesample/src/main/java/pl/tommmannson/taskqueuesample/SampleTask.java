package pl.tommmannson.taskqueuesample;

import android.util.Log;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class SampleTask extends Task<Void> {
    protected SampleTask() {
        super(new TaskParams());
    }

    @Override
    protected void doWork(CancelationToken cancelToken) throws Exception {
        Log.e(SampleTask.class.getSimpleName(), "finished");
    }
}
