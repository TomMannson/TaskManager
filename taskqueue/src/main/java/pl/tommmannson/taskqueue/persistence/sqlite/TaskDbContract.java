package pl.tommmannson.taskqueue.persistence.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Gosia on 2016-11-03.
 */

public class TaskDbContract {

    public static class TaskTable{
        public static final String TABLE_NAME = "task_entity";

        public static final String ID_COLUMN = "_id";
        public static final String IS_UNIQUE_COLUMN = "is_unique";
        public static final String CLASS_NAME_COLUMN = "class_name";
        public static final String GROUP_ID_COLUMN = "group_id";
        public static final String PERSISTENT_COLUMN = "persistent";
        public static final String RETRY_LIMIT_COLUMN = "retry_limit";
        public static final String PRIORITY_COLUMN = "priority";
        public static final String TASK_DATA_COLUMN = "task_data";

        public static final String[] WHERE_SELECT_STAR =  {ID_COLUMN, TASK_DATA_COLUMN };

        public static final String TASK_STATUS_COLUMN = "task_status";

        public static void onCreate(SQLiteDatabase db){
            db.execSQL("Create Table " + TABLE_NAME + "(\n" +
                    "   " + ID_COLUMN + " TEXT PRIMARY KEY     NOT NULL,\n" +
                    "   " + IS_UNIQUE_COLUMN + " TEXT,\n" +
                    "   " + CLASS_NAME_COLUMN + " TEXT,\n" +
                    "   " + GROUP_ID_COLUMN + " TEXT,\n" +
                    "   " + PERSISTENT_COLUMN + " INT,\n" +
                    "   " + RETRY_LIMIT_COLUMN + " INT,\n" +
                    "   " + PRIORITY_COLUMN + " INT,\n" +
                    "   " + TASK_DATA_COLUMN + " BLOB     NOT NULL,\n" +
                    "   " + TASK_STATUS_COLUMN + " INT\n" +
                    ");");
        }
    }
}
