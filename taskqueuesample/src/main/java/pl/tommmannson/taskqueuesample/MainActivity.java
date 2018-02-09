package pl.tommmannson.taskqueuesample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import pl.tommmannson.taskqueue.LifeCycle;
import pl.tommmannson.taskqueue.LifeCycleImpl;
import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskParams;
import pl.tommmannson.taskqueue.progress.ErrorCallback;
import pl.tommmannson.taskqueue.progress.OnManagerReadyListener;
import pl.tommmannson.taskqueue.progress.ResultCallback;

public class MainActivity extends AppCompatActivity implements OnManagerReadyListener, ErrorCallback {

    final static String downloadItemsRequest = "downloadItemsRequest";
    final static String downloadItemsRequest2 = "downloadItemsReques2";
    SampleTask task = null;
    TaskManager manager;
    private SampleTask2 task2;
    LifeCycle life = new LifeCycleImpl();

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

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                task2.run();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerMessageQueueReady(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterMessageQueueReady(this);
        life.stopObserveTaskP(task, intCallback, this);
        life.stopObserveTaskP(task2, stringCallback, this);
    }

    @Override
    public void onTaskManagerReady(long id) {
        task = manager.build(SampleTask.class)
                .id(downloadItemsRequest)
                .getOrCreate();

        task2 = manager.build(SampleTask2.class)
                .id(downloadItemsRequest2)
                .getOrCreate();

        life.observeTaskP(task, intCallback, this);
        life.observeTaskP(task2, stringCallback, this);
    }

    ResultCallback<Integer> intCallback = new ResultCallback<Integer>() {
        @Override
        public void onResult(Task<Integer, ?> task) {
            Integer data = task.getState().getResult();
            Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
        }
    };

    ResultCallback<String> stringCallback = new ResultCallback<String>() {
        @Override
        public void onResult(Task<String, ?> task) {
            Toast.makeText(MainActivity.this, "secondTask"
                    + task.getState().getResult(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onError(String id, Throwable ex) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
