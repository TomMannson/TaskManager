package pl.tommmannson.taskqueue.persistence.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Tomek on 2016-11-03.
 */

class TaskDbContract {

    static class TaskTable{
        static final String TABLE_NAME = "task_entity";

        static final String ID_COLUMN = "_id";
        static final String IS_UNIQUE_COLUMN = "is_unique";
        static final String CLASS_NAME_COLUMN = "class_name";
        static final String GROUP_ID_COLUMN = "group_id";
        static final String PERSISTENT_COLUMN = "persistent";
        static final String RETRY_LIMIT_COLUMN = "retry_limit";
        static final String PRIORITY_COLUMN = "priority";
        static final String MANAGER_ID = "manager_id";
        static final String TASK_DATA_COLUMN = "task_data";

        static final String[] WHERE_SELECT_STAR =  {ID_COLUMN, TASK_DATA_COLUMN, MANAGER_ID};

        static final String TASK_STATUS_COLUMN = "task_status";

        static void onCreate(SQLiteDatabase db){
            db.execSQL("Create Table " + TABLE_NAME + "(\n" +
                    "   " + ID_COLUMN + " TEXT PRIMARY KEY     NOT NULL,\n" +
                    "   " + IS_UNIQUE_COLUMN + " TEXT,\n" +
                    "   " + CLASS_NAME_COLUMN + " TEXT,\n" +
                    "   " + GROUP_ID_COLUMN + " TEXT,\n" +
                    "   " + PERSISTENT_COLUMN + " INT,\n" +
                    "   " + RETRY_LIMIT_COLUMN + " INT,\n" +
                    "   " + MANAGER_ID + " INT,\n" +
                    "   " + PRIORITY_COLUMN + " INT,\n" +
                    "   " + TASK_DATA_COLUMN + " BLOB     NOT NULL,\n" +
                    "   " + TASK_STATUS_COLUMN + " INT\n" +
                    ");");
        }

        static void onDestroy(SQLiteDatabase db){
            db.execSQL("Drop Table " + TABLE_NAME +";");
        }
    }
}
