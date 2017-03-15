package pl.tommmannson.taskqueue;

import java.io.Serializable;

/**
 * Created by tomasz.krol on 2015-11-20.
 */
public class TaskResult<RESULT > implements Serializable {

    public static final int TYPE_STARTED = 0;
    public static final int TYPE_PROGRESS = 1;
    public static final int TYPE_STOPPED = 2;
    public static final int TYPE_FINISHED = 3;
    public static final int TYPE_ERROR = 4;
    public static final int TYPE_CANCEL = 5;

    private Class<?> targetClazz;
    protected RESULT resultData;
    protected int typeOfMessage;

    public TaskResult(RESULT data, int type){
        this.resultData = data;
        this.typeOfMessage = type;

    }

    void setTargetType(Class<?> clazz){
        this.targetClazz = clazz;
    }

    public static <RESULT> TaskResult<RESULT> startResult(RESULT result){
        return new TaskResult<>(result, TYPE_STARTED);
    }

    public static <RESULT> TaskResult<RESULT> progressResult(RESULT result){
        return new TaskResult<>(result, TYPE_PROGRESS);
    }

    public static <RESULT> TaskResult<RESULT> stopedResult(RESULT result){
        return new TaskResult<>(result, TYPE_STOPPED);
    }

    public static <RESULT> TaskResult<RESULT> finishResult(RESULT result){
        return new TaskResult<>(result, TYPE_FINISHED);
    }

    public static <RESULT> TaskResult<RESULT> errorResult(RESULT result){
        return new TaskResult<>(result, TYPE_ERROR);
    }

    public static <RESULT> TaskResult<RESULT> cancelResult(RESULT result){
        return new TaskResult<>(result, TYPE_CANCEL);
    }



    public <T> T getResultData() {
        return (T)resultData;
    }

    Object getResultDataRaw() {
        return resultData;
    }

    public <T> T getResultData(Class<T> clazz) {
        return (T) resultData;
    }

    public int getTypeOfMessage() {
        return typeOfMessage;
    }

    public boolean isStarted(){
        return typeOfMessage == TYPE_STARTED;
    }

    public boolean isProgress(){
        return typeOfMessage == TYPE_PROGRESS;
    }

    public boolean isStaoped(){
        return typeOfMessage == TYPE_STOPPED;
    }

    public boolean isFinised(){
        return typeOfMessage == TYPE_FINISHED;
    }

    public boolean isError(){
        return typeOfMessage == TYPE_ERROR;
    }

    public boolean isCanceled(){
        return typeOfMessage == TYPE_CANCEL;
    }

    public Class<?> getTargetType() {
        return targetClazz;
    }
}
