package pl.tommmannson.taskqueuesample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskContainer;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskResult;
import pl.tommmannson.taskqueue.progress.TaskCallback;

public class MainActivity extends AppCompatActivity implements TaskCallback, TaskContainer.TaskContainerCallback {

    final static String downloadItemsRequest = "downloadItemsRequest";
    SampleTask task = null;//new SampleTask();
    SampleTask2 task2 = new SampleTask2();
    TaskManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = TaskManager.getInstance(1);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        task = new SampleTask();
        task.setId(downloadItemsRequest);

        TaskContainer<SampleTask> taskcontainer = manager.findTaskById(downloadItemsRequest);
        if (taskcontainer.isReady()) {
            task = taskcontainer.getTask();
            manager.registerCallback(task, MainActivity.this);
        } else {
            taskcontainer.executeAsync(this);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

//                if (task == null) {
//
////                    SharedPreferences.Editor editor = prefs.edit();
////                    editor.putString(task.getClass().getCanonicalName(), task.getId());
////                    editor.apply();
//                }

                task.run(1);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerCallback(task, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterCallback(task, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(String id, TaskResult result) {
        switch (id) {
            case downloadItemsRequest: {

            }
        }
        Void data = task.getTaskResult(result);
        Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String id, Throwable ex) {

    }

    @Override
    public void onTaskLoaded(String id, Task task) {
        MainActivity.this.task = (SampleTask) task;
        if(task == null){
            this.task = new SampleTask();
            this.task.setId(downloadItemsRequest);
        }
        manager.registerCallback(task, MainActivity.this);
    }
}
