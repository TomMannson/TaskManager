package pl.tommmannson.taskqueue.queues;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import pl.tommmannson.taskqueue.Task;
import pl.tommmannson.taskqueue.persistence.Serializer;

/**
 * Created by tomasz.krol on 2016-02-02.
 */
public class ConcurrentTaskQueue implements TaskQueue {


    private BlockingQueue<Task<?>> workQueue = null;
    private BlockingQueue<Task<?>> uniqueTasks = null;

    Comparator<? super Task<?>> DEFAULT_TASK_COMPARATOR = new Comparator<Task<?>>() {
        @Override
        public int compare(Task lhs, Task rhs) {
            return lhs.getPriority() - rhs.getPriority();
        }
    };

    Serializer serializer;

    public ConcurrentTaskQueue(Serializer serializer /*String path*/){
        this.serializer = serializer;
        this.workQueue = new PriorityBlockingQueue<>(100 , DEFAULT_TASK_COMPARATOR);
        this.uniqueTasks = new LinkedBlockingQueue<>(100);
    }

    public synchronized boolean hasTasksWaitingForExecution(){

        return !workQueue.isEmpty();
    }

    public synchronized boolean pushToQueue(Task task){

        boolean taskCanBeAddedToQueue = (!workQueue.contains(task) && !uniqueTasks.contains(task));
        if(taskCanBeAddedToQueue) {
            workQueue.add(task);
        }

        return taskCanBeAddedToQueue;
    }

    public synchronized Task getTaskFromQueue( ) throws InterruptedException {
        Task task = workQueue.peek();

        if (task.isUnique() || task.getGroupId() != null) {
            uniqueTasks.add(task);
        }
        workQueue.take();

        return task;
    }

    public synchronized boolean removeWaiting(Task task){
        return workQueue.remove(task);
    }

    public synchronized boolean removeProcessing(Task task){
        return uniqueTasks.remove(task);
    }

    public synchronized void moveFromExecutingToWaiting(Task task){
        workQueue.add(task);
        uniqueTasks.remove(task);
    }

    public synchronized void clear(){
        workQueue.clear();
        uniqueTasks.clear();
    }

    @Override
    public List<Task<?>> getFullList() {
        List<Task<?>> fullList = new ArrayList<>();
        fullList.addAll(uniqueTasks);
        fullList.addAll(workQueue);
        return fullList;
    }

    @Override
    public void addFullList(List<Task<?>> listToAdd) {
        workQueue.addAll(listToAdd);
    }

//    public synchronized void persist(){
//        try {
//            FileOutputStream fileOut =
//                    new FileOutputStream(new File(pathToHoldSavedTasks));
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            BackupTasksManager cache = new BackupTasksManager();
//            cache.setWorkQueue(workQueue);
//            cache.setUniqueTasks(uniqueTasks);
//            out.writeObject(cache);
//            out.close();
//            fileOut.close();
//        } catch (IOException ex) {
//            ex.toString();
//        }
//    }
//
//    public synchronized void load(){
//        loadingBackapedTasksInProgress = true;
//        new Thread() {
//            public void run() {
//                try {
//                    FileInputStream fileOut =
//                            new FileInputStream(new File(pathToHoldSavedTasks));
//                    ObjectInputStream in = new ObjectInputStream(fileOut);
//                    BackupTasksManager cache = (BackupTasksManager) in.readObject();
//                    workQueue.addAll(cache.getWorkQueue());
//                    workQueue.addAll(cache.getUniqueTasks());
//                    fileOut.close();
//                } catch (IOException ex) {
//                    ex.toString();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                loadingBackapedTasksInProgress = false;
//            }
//        }.start();
//    }
//
//    public synchronized boolean isLoadingBackapedTasksInProgress(){
//        return this.loadingBackapedTasksInProgress;
//    }

    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(workQueue);
        tasks.addAll(uniqueTasks);
        return tasks;
    }

}
