package pl.tommmannson.taskqueuesample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;
import pl.tommmannson.taskqueue.progress.TaskCallback;

public class MainActivity extends AppCompatActivity implements TaskCallback<SampleTask>, OnManagerReadyListener {

    final static String downloadItemsRequest = "downloadItemsRequest";
    SampleTask task = null;
    TaskManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = TaskManager.getInstance(1);

        manager.registerMessageQueueReady(this);
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
        manager.registerCallback(downloadItemsRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterCallback(downloadItemsRequest, this);
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
    public void onResult(String id, SampleTask task) {

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
    }
}
