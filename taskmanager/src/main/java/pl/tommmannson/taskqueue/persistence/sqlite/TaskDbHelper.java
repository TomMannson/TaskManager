package pl.tommmannson.taskqueue.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gosia on 2016-11-03.
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "taskDB";
    private static final int DB_VERSION = 2;

    TaskDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TaskDbContract.TaskTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TaskDbContract.TaskTable.onDestroy(db);
        onCreate(db);
    }
}
