import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class PoolExample{

    public static void main(String[] args) throws InterruptedException{
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        RejectedExecutionHandler handler = new RejectedExecutionHandler(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor){
                if ( !executor.isShutdown() ) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        };
        executor.setRejectedExecutionHandler(handler);

        AtomicInteger count = new AtomicInteger(0);

        AtomicInteger inProgress = new AtomicInteger(0);


        for (int i = 0; i < 30; i++) {
            final int number = i;
            Thread.sleep(10);

            System.out.println("creating #" + number);
            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                System.out.println("start #" + number + ", in progress: " + working);
                Thread.sleep(Math.round(1000 + Math.random() * 2000));

                working = inProgress.decrementAndGet();
                System.out.println("end #" + number + ", in progress: " + working + ", done tasks: " + count.incrementAndGet());
                return null;
            });
        }
    }
}
