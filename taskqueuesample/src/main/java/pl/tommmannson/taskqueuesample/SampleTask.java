package pl.tommmannson.taskqueuesample;

import android.util.Log;

import java.io.InvalidClassException;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.Task1;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public class SampleTask extends Task1<Integer> {
    public SampleTask(TaskParams params) {
        super(params);
    }


    @Override
    protected void doWork(CancelationToken cancelToken) throws Exception {
        Log.e(SampleTask.class.getSimpleName(), "finished");
        Thread.sleep(4000);
        notifyResult(1);
//        notifyError(new InvalidClassException("asd"));
//        SampleTask2 task2 = new SampleTask2();
//        task2.setId("secondTask");
//        task2.run(1);
    }
}
