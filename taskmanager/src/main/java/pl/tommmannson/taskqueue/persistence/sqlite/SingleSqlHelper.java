package pl.tommmannson.taskqueue.persistence.sqlite;

import android.content.Context;

/**
 * Created by TomekAndGosia on 15.03.2017.
 */

public class SingleSqlHelper {
    static TaskDbHelper instance;
    public synchronized static TaskDbHelper getInstance(Context ctx){
        if(instance == null) {
            instance = new TaskDbHelper(ctx);
        }
        return instance;
    }
}
