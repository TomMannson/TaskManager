package pl.tommmannson.taskqueue.messaging;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;

/**
 * Created by tomasz.krol on 2016-12-06.
 */

public interface Message {

    public int getType();

    public TaskManagementInterface management();

}
