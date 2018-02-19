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
    private TaskObjectSerialisator creator = new TaskObjectSerialisator();

    public SqlSerializer(Context ctx, int managerId) {
        dbHelper = SingleSqlHelper.getInstance(ctx);
        this.managerId = managerId;
    }

    @Override
    public void persist(TaskQueue queue, Task taskToPersist) {

        if (!taskToPersist.isPersistent()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues value = null;
        try {
            value = creator.createValueFormObject(taskToPersist);
        } catch (IOException e) {
            return;
        }

        value.put(TaskDbContract.TaskTable.MANAGER_ID, "" + managerId);

        if (taskToPersist.getState().getStatus() == TaskStatus.NotExistsInQueue) {
            createTaskInDB(db, value);
        } else {
            int data = editTaskInDB(taskToPersist, db, value);

            if (data == 0) {
                createTaskInDB(db, value);
            }
        }
    }

    @Override
    public void remove(TaskQueue queue, Task taskToPersist) {
        if (!taskToPersist.isPersistent()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        deleteTaskInDB(taskToPersist, db);
    }

    private int editTaskInDB(Task taskToPersist, SQLiteDatabase db, ContentValues value) {
        return db.update(TaskDbContract.TaskTable.TABLE_NAME,
                value, TaskDbContract.TaskTable.ID_COLUMN + "=?",
                new String[]{taskToPersist.getId()});
    }

    private int deleteTaskInDB(Task taskToPersist, SQLiteDatabase db) {
        return db.delete(TaskDbContract.TaskTable.TABLE_NAME,
                TaskDbContract.TaskTable.ID_COLUMN + "=?",
                new String[]{taskToPersist.getId()});
    }

    private void createTaskInDB(SQLiteDatabase db, ContentValues value) {
        long id = db.insert(TaskDbContract.TaskTable.TABLE_NAME, null,
                value);
        Log.d("SqlSerializer", "insedted");
        Log.d("SqlSerializer", "id= " + id);
    }

    @Override
    public void restore(TaskQueue queue) {
        Cursor result = null;
        List<Task<?, ?>> list = new ArrayList<>();
        try {
            result = dbHelper.getReadableDatabase()
                    .query(TaskDbContract.TaskTable.TABLE_NAME,
                            TaskDbContract.TaskTable.WHERE_SELECT_STAR,
                            TaskDbContract.TaskTable.MANAGER_ID + "=?", new String[]{"" + managerId}, null, null, null);

            result.getCount();

            if (result.moveToFirst()) {
                do {

                    Task task = creator.createObjectFromCursor(result);
                    if (task != null) {
                        list.add(task);
                    }

                } while (result.moveToNext());
                queue.addFullList(list);
            }
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }
}
