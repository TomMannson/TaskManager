package pl.tommmannson.taskqueue.persistence.serialization;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.queues.TaskQueue;

/**
 * Created by tomasz.krol on 2016-10-24.
 */

public class FileSerializer implements Serializer {

    public String pathToHoldSavedTasks;

    public FileSerializer(Context ctx, int idOfManager) {
        pathToHoldSavedTasks = new File(ctx.getApplicationContext().getFilesDir(), "file_" + idOfManager).getAbsolutePath();
    }

    public synchronized void persist(TaskQueue queue, Task taskToPersist) {

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(new File(pathToHoldSavedTasks));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            List<Task<?, ?>> listToWrite = new ArrayList<>();
            for (Task<?, ?> task: queue.getFullList()) {
                if(task.isPersistent()) {
                    listToWrite.add(task);
                }
            }
            out.writeObject(listToWrite);
            out.close();

        } catch (IOException ex) {
            ex.toString();
        }
    }

    public synchronized void restore(final TaskQueue queue) {
        try {
            FileInputStream fileOut =
                    new FileInputStream(new File(pathToHoldSavedTasks));
            ObjectInputStream in = new ObjectInputStream(fileOut);
            queue.addFullList((List<Task<?, ?>>) in.readObject());
            fileOut.close();
        } catch (IOException ex) {
            ex.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
