package pl.tommmannson.taskqueuesample;

import android.util.Log;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class SampleTask extends Task<Void> {
    protected SampleTask() {
        super(new TaskParams().persistent(true).unique(true));
    }

    @Override
    protected void doWork(CancelationToken cancelToken) throws Exception {
        Log.e(SampleTask.class.getSimpleName(), "finished");
        Thread.sleep(2000);
        notifyResult(TaskResult.<Void>progressResult(null));
        SampleTask2 task2 = new SampleTask2();
        task2.setId("secondTask");
        task2.run(1);
    }
}
