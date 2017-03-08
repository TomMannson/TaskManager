package pl.tommmannson.taskqueue.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


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

    private final TaskDbHelper dbHelper;
    TaskObjectSerialisator creator = new TaskObjectSerialisator();

    public SqlSerializer(Context ctx) {
        dbHelper = new TaskDbHelper(ctx);
    }

    @Override
    public void persist(TaskQueue queue, Task taskToPersist) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues value = creator.createValueFormObject(taskToPersist);

        if (taskToPersist.getTaskStatus() == TaskStatus.NotExistsInQueue) {
            taskToPersist.setCreated(true);
            long id = db.insert(TaskDbContract.TaskTable.TABLE_NAME, null,
                    value);
            Log.d("SqlSerializer", "insedted");
            Log.d("SqlSerializer", "id= " + id);

        } else {
            db.update(TaskDbContract.TaskTable.TABLE_NAME,
                    value, TaskDbContract.TaskTable.ID_COLUMN + "=?",
                    new String[]{taskToPersist.getId()});
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
                            "", new String[0], null, null, null);

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
}
