package pl.tommmannson.taskqueue.cancelation;

/**
 * Created by tomasz.krol on 2016-01-29.
 */
public class CancelationToken {

    private boolean canceled = false;

    public void cancel(){
        canceled = true;
    }

    private void throwCancelSignal(){
        if(canceled){
            throw new CancelationException();
        }
    }

    public boolean isCanceled(){
        return canceled;
    }

    public void forceCancellation(){
        canceled = true;
        throwCancelSignal();
    }
}
