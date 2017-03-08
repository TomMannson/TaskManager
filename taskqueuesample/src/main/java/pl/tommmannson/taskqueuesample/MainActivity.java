package pl.tommmannson.taskqueuesample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.TaskContainer;
import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.progress.TaskCallback;

public class MainActivity extends AppCompatActivity implements TaskCallback<Void> {

    SampleTask task = new SampleTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = getSharedPreferences("global", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        String taskId = prefs.getString(SampleTask.class.getCanonicalName(), null);
//        if(taskId != null){
//            TaskContainer<SampleTask> taskcontainer = SampleTask.findById(taskId);
//            if(taskcontainer.isReady()){
//                task = taskcontainer.getTask();
//            }
//            else{
//                taskcontainer.executeAsync(new TaskContainer.TaskContainerCallback<SampleTask>() {
//                    @Override
//                    public void onTaskLoaded(SampleTask task) {
//                        MainActivity.this.task = task;
//                    }
//                });
//            }
//        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(task == null) {
                    task = new SampleTask();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(task.getClass().getCanonicalName(), task.getId());
                    editor.apply();
                }

                task.run();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskManager.DEFAULT.registerCallback(task, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskManager.DEFAULT.unregisterCallback(task, this);
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
    public void onResult(Void result) {

    }

    @Override
    public void onError(Throwable ex) {

    }
}
