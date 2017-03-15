package pl.tommmannson.taskqueuesample;

import android.util.Log;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class SampleTask2 extends Task<Integer> {
    protected SampleTask2() {
        super(new TaskParams().persistent(true));
    }

    @Override
    protected void doWork(CancelationToken cancelToken) throws Exception {
        Log.e(SampleTask2.class.getSimpleName(), "finished");
        notifyResult(TaskResult.finishResult(1));
    }
}
