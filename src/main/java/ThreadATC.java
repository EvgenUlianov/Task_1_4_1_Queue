import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadATC extends ThreadParticipant{

    public ThreadATC(Queue<String> calls) {
        super(calls);
    }

    @Override
    public void run( ) {
        final int timeOut = 1000;
        final int numberOfCallsWanted = 30;

        for (int i = 1; i <= numberOfCallsWanted; i++) {
            BasicFunctions.sleep(timeOut);
            String call = String.format("Звонок № %d", i);
            if (isArrayBlockingQueue) {
                boolean successful = false;
                while (!successful)
                    try {
                        calls.add(call);
                        successful = true;
                        printState(successful, call);
                    } catch (IllegalStateException e){
                        printState(successful, call);
                        BasicFunctions.sleep(timeOut);
                    }
            } else {
                calls.add(call);
                printState(true, call);
            }
        }

        isEnded = true;
    }

    private void printState(boolean successful, String call){
        String state = successful ? "Новый" : "не прошел";
        if (isConcurrentLinkedDeque) {
            System.out.printf("%s входящий %s.\n", state,  call);
        } else {
            System.out.printf("%s входящий %s. (в очереди %d).\n", state, call, calls.size());
        }
    }

}
