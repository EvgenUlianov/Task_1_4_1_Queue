import java.util.Queue;
import java.util.concurrent.*;

abstract public class ThreadParticipant extends Thread implements Runnable{
    protected Queue<String> calls;
    final protected boolean isConcurrentLinkedDeque;
    final protected boolean isArrayBlockingQueue;
    protected boolean isEnded;
    public boolean getIsEnded(){
        System.out.printf("%s %s\n", getName(), isEnded? "завершен": "продолжается");
        return isEnded;
    }


    public ThreadParticipant(Queue<String> calls) {
        this.calls = calls;
        isConcurrentLinkedDeque = (calls.getClass() == ConcurrentLinkedDeque.class);
        isArrayBlockingQueue = (calls.getClass() == ArrayBlockingQueue.class);
        isEnded = false;
    }


}
