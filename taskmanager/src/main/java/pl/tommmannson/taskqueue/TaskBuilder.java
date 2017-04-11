package pl.tommmannson.taskqueue;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import pl.tommmannson.taskqueue.bootstraping.TaskManagementInterface;

/**
 * Created by tomasz.krol on 2017-04-11.
 */

public class TaskBuilder<T extends Task> {

    Class<T> taskClass;

    TaskManager manager;
    TaskManagementInterface service;
    TaskParams params = new TaskParams();
    String id;

    public TaskBuilder(Class<T> taskClass) {
        this.taskClass = taskClass;
    }

    public TaskBuilder manager(TaskManager manager) {
        this.manager = manager;
        return this;
    }

    public TaskBuilder params(TaskParams params) {
        this.params = params;
        return this;
    }

    TaskBuilder service(TaskManagementInterface serivce) {
        this.service = serivce;
        return this;
    }

    public TaskBuilder id(String id) {
        this.id = id;
        return this;
    }

    public T get(){
        Task task = service.findTaskById(id);
        if(task != null && task.getClass().equals(taskClass)){
            return (T) task;
        }
        return null;
    }

    public T create(){
        try {
            T task =  taskClass.getConstructor(TaskParams.class).newInstance(params);
            task.setId(id == null ? UUID.randomUUID().toString() : id);
            task.setTaskmanager(manager);
            return task;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T getOrCreate(){
        T task = get()
    }
}
