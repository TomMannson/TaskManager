package pl.tommmannson.taskqueue.persistence.serialization;

import android.content.Context;

import pl.tommmannson.taskqueue.persistence.Configuration;

/**
 * Created by tomasz.krol on 2017-03-15.
 */

public class FileConfiguration extends Configuration {

    Context ctx;
    String filePath;


    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
