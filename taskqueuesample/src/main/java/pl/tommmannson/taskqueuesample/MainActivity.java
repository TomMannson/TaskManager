package pl.tommmannson.taskqueuesample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;
import pl.tommmannson.taskqueue.progress.TaskCallback;
import pl.tommmannson.taskqueue.utils.CallForwarder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements TaskCallback,  OnManagerReadyListener {

    final static String downloadItemsRequest = "downloadItemsRequest";
    final static String downloadItemsRequest2 = "downloadItemsReques2";
    SampleTask task = null;
    TaskManager manager;
    private SampleTask2 task2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = TaskManager.getInstance(1);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                task.run();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerMessageQueueReady(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterMessageQueueReady(this);
        if(task != null)
            task.unregister(this);
    }

//    @Override
//    public void onResult(String id, TaskState result) {
//        switch (id) {
//            case downloadItemsRequest: {
//                Integer data = result.getResult().getResultData();
//                Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
//                break;
//            }
//            default: {
//                Toast.makeText(this, "secondTask", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onResult(String id, Task task) {
        if(this.task == task){
            Toast.makeText(getApplicationContext(), "SimpleTask", Toast.LENGTH_SHORT).show();
        }
        else {
            CallForwarder.forwardCall(this, id, task);
        }
    }

    @Override
    public void onProgress(String id, TaskResult result) {

    }


    public void onResult(String id, SampleTask task) {

        Toast.makeText(getApplicationContext(), "SimpleTask", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String id, Throwable ex) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskManagerReady(long id) {
        task = manager.build(SampleTask.class)
                .id(downloadItemsRequest)
                .params(new TaskParams().retryLimit(10).retryStrategy(2))
                .getOrCreate();

        task.register(this);

        task2 = manager.build(SampleTask2.class)
                .id(downloadItemsRequest2)
                .params(new TaskParams().retryLimit(10).retryStrategy(2))
                .getOrCreate();

        task2.register(this);

    }
}
