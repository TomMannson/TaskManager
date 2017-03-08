package pl.tommmannson.taskqueue.cancelation;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class CancelationToken {

    boolean canceled = false;

    public void cancel(){
        canceled = true;
    }

    public void throwCancelSignal(){
        if(canceled){
            throw new CancelationException();
        }
    }

    public void forceCancellation(){
        canceled = true;
        throwCancelSignal();
    }
}
