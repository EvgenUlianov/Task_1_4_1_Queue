import java.util.Queue;
import java.util.Random;

public class ThreadSpecialist extends ThreadParticipant{
    private ThreadATC atc;
    public ThreadSpecialist(Queue<String> calls, ThreadATC atc, int number) {
        super(calls);
        this.setName(String.format("Специалист(ка) %d", number));
        this.atc = atc;
    }


    @Override
    public void run() {
        Random random = new Random();
        final int timeOut = 3000 + random.nextInt(1000);
        System.out.printf("%s в работе.\n", getName());
        BasicFunctions.sleep(timeOut);
        while (atc.isAlive() || !calls.isEmpty()) {
            if (!calls.isEmpty()){
                String call = calls.poll();
                System.out.printf("%s принял %s.\n", getName() , call);
            }
            BasicFunctions.sleep(timeOut);
        }

        isEnded = true;
    }
}
