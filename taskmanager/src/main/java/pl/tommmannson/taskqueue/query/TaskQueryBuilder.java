package pl.tommmannson.taskqueue.query;

import pl.tommmannson.taskqueue.TaskManager;
import pl.tommmannson.taskqueue.TaskQuery;

/**
 * Created by tomasz.krol on 2017-04-10.
 */

public class TaskQueryBuilder {

    String[] ids;
    String[] groups;
    private boolean onlyFinished;
    private boolean lastInGroup;
    private boolean sortByDate;

    TaskManager manager;

    public TaskQueryBuilder findTaskById(String id) {
        this.ids = new String[] {id};
        return this;
    }

    public TaskQueryBuilder findTaskById(String[] ids) {
        this.ids = ids.clone();
        return this;
    }

    public TaskQueryBuilder findTaskByGroup(String group) {
        this.groups = new String[] {group};
        return this;
    }

    public TaskQueryBuilder findTaskByGroup(String[] groups) {
        this.groups = groups.clone();
        return this;
    }

    public TaskQueryBuilder onlyAfterExecution(boolean onlyFinished) {
        this.onlyFinished = onlyFinished;
        return this;
    }

    public TaskQueryBuilder howLastinGroup(boolean lastInGroup) {
        this.lastInGroup = lastInGroup;
        return this;
    }

    public TaskQueryBuilder sortByDate(boolean sortByDate) {
        this.sortByDate = sortByDate;
        return this;
    }

    public TaskQuery build(){
        TaskQuery query;
        return query;
    }
}
