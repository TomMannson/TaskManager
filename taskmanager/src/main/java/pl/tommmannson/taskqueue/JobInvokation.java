package pl.tommmannson.taskqueue;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.tommmannson.taskqueue.cancelation.CancelationException;
import pl.tommmannson.taskqueue.cancelation.CancelationToken;
import pl.tommmannson.taskqueue.config.di.DependencyInjector;
import pl.tommmannson.taskqueue.persistence.Serializer;
import pl.tommmannson.taskqueue.persistence.TaskStatus;
import pl.tommmannson.taskqueue.progress.ProgressManager;
import pl.tommmannson.taskqueue.progress.TaskCallback;

/**
 * Created by tomasz.krol on 2018-02-08.
 */

public class JobInvokation implements Runnable {

    private Map<String, List<TaskCallback>> callbacks = new HashMap<>();
    private Map<Task<?, ?>, CancelationToken> cancelation = new HashMap<>();
    private Map<String, Task<?, ?>> tasks = new HashMap<>();
    private Serializer serializer;
    DependencyInjector injector = null;

    private final JobParameters job;
    private JobService service;

    JobInvokation(JobParameters job, JobService service) {

        this.service = service;
        this.job = job;
    }

    public void invoke() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public boolean invokeError() {

        if (tasks.get(job.getExtras().getString("TAG")).getState().getStatus() == TaskStatus.FailFinished) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JobInvokation.this.invokeError();
                }
            });
            thread.start();
            return false;
        } else if (tasks.get(job.getExtras().getString("TAG"))
                .getState().getStatus() == TaskStatus.AddedToQueue) {
            return true;
        }
        return true;
    }

    @Override
    public void run() {
        Task request = null;
        try {

            request = tasks.get(job.getExtras().getString("TAG"));

            if (request == null) {
                return;
            }

            performTaskWork(request);
            request.sendResult();
            service.jobFinished(job, false);

        } catch (InterruptedException ex) {
//                concurrentTaskQueue.moveFromExecutingToWaiting(request);
        } catch (CancelationException ex) {
            Log.d(this.getClass().getName(), String.format("%s canceled", request));

            if (request != null) {

                request.notifyResult(TaskResult.cancelResult(null));

                cancelation.remove(request);
//                    concurrentTaskQueue.removeProcessing(request);
                request.setExecutionStatus(TaskStatus.Canceled);
                service.jobFinished(job, false);
            }
        } catch (Exception ex) {

            Log.e("TaskService", "Exception during request process on  ");
            ex.printStackTrace();

            if (request != null) {
//                if (request.nextRetry()) {
//                    //concurrentTaskQueue.moveFromExecutingToWaiting(request);
//                    final Task requestToRetry = request;
//                    concurrentTaskQueue.moveToPending(requestToRetry, new RetryOperation() {
//                        @Override
//                        public void doOnRetry() {
//                            if (!cancelation.get(requestToRetry).isCanceled()) {
//                                workerThreadPool.execute(TaskQueueThread.this);
//                            }
//                        }
//                    });
////                        workerThreadPool.execute(this);
//                } else {
//                    concurrentTaskQueue.removeProcessing(request);
//                }

                request.setExecutionStatus(TaskStatus.FailFinished);
                service.jobFinished(job, false);


            }
        } finally {
            if (request != null) {
                request.recycle();
                request.detachProgressManager();
                serializer.persist(null, request);
            }
        }
    }

    private void performTaskWork(Task request) throws Exception {
        CancelationToken token = cancelation.get(request);
        if (token == null) {
            token = new CancelationToken();
            cancelation.put(request, token);
        }

        ProgressManager manager = request.createProgressManager(request.getId(), callbacks);
        request.setExecutionStatus(TaskStatus.InProgress);

        if (injector != null) {
            injector.inject(request);
        }
        request.attachProgressManager(manager);
        request.doWork(token);
        if (request.getState().getException() != null) {
            throw request.getState().getException();
        }
        cancelation.remove(request);

        request.setExecutionStatus(TaskStatus.SuccessfullyFinished);
    }

    public boolean performErrorHandling() {
        return false;
    }

    public void init(Map<String, Task<?, ?>> tasks, Map<String, List<TaskCallback>> callbacks, Map<Task<?, ?>, CancelationToken> cancelation, DependencyInjector injector, Serializer serializer) {

        this.tasks = tasks;
        this.callbacks = callbacks;
        this.cancelation = cancelation;
        this.injector = injector;
        this.serializer = serializer;
    }
}
