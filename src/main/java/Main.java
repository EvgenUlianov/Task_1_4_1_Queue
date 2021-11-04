import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Задача 1. Колл-центр");
        final int  limitOfCalls = 10;
        final int  numberOfClasses = 9;

        Queue<String> calls;
        Random random = new Random();
        final int variantOfQueue = 1 + random.nextInt(numberOfClasses - 1);
        int[] excludedNumbers = {6, 9};
        for (int excludedNumber: excludedNumbers) {
            if (variantOfQueue == excludedNumber)
                return;
        }

        calls = switch (variantOfQueue) {

            //---Блокировка потоков (без возможности «честности»)
            //---Нельзя хранить null
            //пусть будет
            case 1 -> new PriorityBlockingQueue();

            //+++Быстрая неблокирующая работа
            //---Нельзя хранить null
            //пусть будет
            case 2 -> new ConcurrentLinkedQueue();

            //+++Быстрая неблокирующая работа
            //+++Реализация двунаправленной очереди
            //---Риски разрастания очереди при работе с большим объемом данных
            //---Дорогая операция size()
            //Эффективность коллекции на 40-50% ниже аналогичной коллекции без LIFO
            //пусть будет, size() исключил в процедуре "ThreadATC.printState()"
            case 3 -> new ConcurrentLinkedDeque();

            //+++Возможность «честных» блокировок
            //---Блокировка потоков
            //---Очередь фиксированного размера
            //---Нельзя хранить null
            //пусть будет, риск переполнения учел в "ThreadATC.run()"
            case 4 -> new ArrayBlockingQueue(limitOfCalls);

            //---Больше затрат памяти, чем ArrayBlockingQueue
            //---Нельзя хранить null
            //пусть будет
            case 5 -> new LinkedBlockingQueue();

            //+++Возможность «честных» блокировок
            //---Блокировка потоков
            //---Нельзя хранить null
            //нет: потому что в моей программе это не получится реаоизовать
            case 6 -> new SynchronousQueue();

            //+++Реализация двунаправленной очереди
            //---Менее эффективная работа, чем у LinkedBlockingQueue
            //---Нельзя хранить null
            //пусть будет
            case 7 -> new LinkedBlockingDeque();

            //+++Более гибкая работа, чем с SynnchronousQueue
            //---Блокировка потоков
            //---Нельзя хранить null
            //пусть будет
            case 8 -> new LinkedTransferQueue();

            //---Блокировка потоков (без возможности «честности»)
            //---Специфичная коллекция, нельзя хранить элементы кроме implements Delayed
            //нет: слишком сильно выбивается, ради такой коллекции пришлось бы весь код переписать
            case 9 -> new DelayQueue();

            default -> null;
        };
        if (calls == null)
            return;

        System.out.printf("Using %s\n", calls.getClass().getSimpleName());


        final int numberOfATC = 1;
        final int numberOfSpecialiststs = 3;



        final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfATC + numberOfSpecialiststs);

        List<ThreadParticipant> threads = new CopyOnWriteArrayList();//numberOfATC + numberOfSpecialiststs
        ThreadATC atc = new ThreadATC(calls);
        threads.add(atc);
        threadPool.execute(atc);
        for (int i = 0; i < numberOfSpecialiststs; i++){
            ThreadSpecialist specialist = new ThreadSpecialist(calls, atc, i + 1);
            threads.add(specialist);
            threadPool.execute(specialist);
        }

        final int timeOutShort = 500;
        final int timeOutMiddle = 5_000;
        final int timeOutLong = 15_000;
        BasicFunctions.sleep(timeOutLong);
        boolean needToClose = false;
        while (!needToClose) {
            needToClose = needToClose(threads);
            BasicFunctions.sleep(timeOutMiddle);
        }
        BasicFunctions.sleep(timeOutShort);
        System.out.println("Звонилка завершена");
        threads.stream().forEach((thread) -> thread.interrupt());
        threadPool.shutdownNow();
    }

    static boolean needToClose(List<ThreadParticipant> threads){
        return (threads.stream()
            .allMatch((thread) -> thread.getIsEnded()));
    }
}
