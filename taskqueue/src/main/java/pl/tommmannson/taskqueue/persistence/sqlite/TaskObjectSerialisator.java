package pl.tommmannson.taskqueue.persistence.sqlite;

import android.content.ContentValues;
import android.database.Cursor;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pl.tommmannson.taskqueue.Task;

/**
 * Created by Gosia on 2016-11-03.
 */

public class TaskObjectSerialisator {

    public Task createObjectFromCursor(Cursor cursor) {
        Task task = null;
        try {
            byte[] data = cursor.getBlob(cursor.getColumnIndex(TaskDbContract.TaskTable.TASK_DATA_COLUMN));
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            task = (Task) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    public ContentValues createValueFormObject(Task task) {

        try {
            if (task != null) {
                ByteArrayOutputStream bais = new ByteArrayOutputStream();
                ContentValues value = new ContentValues();
                value.put(TaskDbContract.TaskTable.ID_COLUMN,
                        task.getId());
                ObjectOutputStream ois = new ObjectOutputStream(bais);
                ois.writeObject(task);
                ois.flush();

                value.put(TaskDbContract.TaskTable.TASK_DATA_COLUMN, bais.toByteArray());
                return value;

            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
