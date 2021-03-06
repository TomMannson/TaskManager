package pl.tommmannson.taskqueue.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.queues.TaskQueue;

/**
 * Created by tomasz.krol on 2016-11-04.
 */

public class SqlSerializer implements Serializer {

    private TaskDbHelper dbHelper;
    private int managerId;
    TaskObjectSerialisator creator = new TaskObjectSerialisator();

    public SqlSerializer(Context ctx) {
        dbHelper = SingleSqlHelper.getInstance(ctx);
    }

    @Override
    public void persist(TaskQueue queue, Task taskToPersist) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues value = null;
        try {
            value = creator.createValueFormObject(taskToPersist);
        } catch (IOException e) {
            Log.e("SqlSerializer", "insedted", e);
            return;
        }

        if (taskToPersist.getTaskStatus() == TaskStatus.NotExistsInQueue) {
            taskToPersist.setCreated(true);
            long id = db.insert(TaskDbContract.TaskTable.TABLE_NAME, null,
                    value);
            Log.d("SqlSerializer", "insedted");
            Log.d("SqlSerializer", "id= " + id);

        } else {
            int data = db.update(TaskDbContract.TaskTable.TABLE_NAME,
                    value, TaskDbContract.TaskTable.ID_COLUMN + "=?",
                    new String[]{taskToPersist.getId()});
            if (data == 0) {
                taskToPersist.setCreated(true);
                long id = db.insert(TaskDbContract.TaskTable.TABLE_NAME, null,
                        value);
                Log.d("SqlSerializer", "insedted");
                Log.d("SqlSerializer", "id= " + id);
            }
            Log.d("SqlSerializer", "edited");
        }
    }

    @Override
    public void restore(TaskQueue queue) {
        Cursor result = null;
        List<Task<?>> list = new ArrayList<>();
        try {
            result = dbHelper.getReadableDatabase()
                    .query(TaskDbContract.TaskTable.TABLE_NAME,
                            TaskDbContract.TaskTable.WHERE_SELECT_STAR,
                            TaskDbContract.TaskTable.MANAGER_ID+"+?", new String[]{"" + managerId}, null, null, null);

            result.getCount();

            if (result.moveToFirst()) {
                do {

                    Task task = creator.createObjectFromCursor(result);
                    if (task != null)
                        list.add(task);

                } while (result.moveToNext());
                queue.addFullList(list);
            }
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }

    public void setConfig(TaskDbHelper healper, int managerId) {
        this.dbHelper = healper;
        this.managerId = managerId;
    }
}
