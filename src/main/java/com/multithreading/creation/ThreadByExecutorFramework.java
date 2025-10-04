package main.java.com.multithreading.creation;

import java.util.concurrent.*;

/**
 * Demo: Creating threads using Executor Framework
 */
public class ThreadByExecutorFramework {

    static class Task implements Runnable {
        private String taskName;
        private int duration;

        public Task(String taskName, int duration) {
            this.taskName = taskName;
            this.duration = duration;
        }

        @Override
        public void run() {
            System.out.println("Task " + taskName + " started on thread: " + Thread.currentThread().getName());

            try {
                Thread.sleep(duration * 1000);
                System.out.println("Task " + taskName + " completed after " + duration + " seconds");
            } catch (InterruptedException e) {
                System.out.println("Task " + taskName + " interrupted");
            }
        }
    }

    static class CallableTask implements Callable<String> {
        private String taskName;
        private int processingTime;

        public CallableTask(String taskName, int processingTime) {
            this.taskName = taskName;
            this.processingTime = processingTime;
        }

        @Override
        public String call() throws Exception {
            System.out.println("Callable Task " + taskName + " started on thread: " + Thread.currentThread().getName());
            Thread.sleep(processingTime * 1000);
            return "Result from " + taskName + " after " + processingTime + " seconds";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Thread Creation using Executor Framework ===");

        // 1. Fixed Thread Pool
        System.out.println("\n1. Fixed Thread Pool Demo:");
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            fixedPool.submit(new Task("FixedPool-Task-" + i, 2));
        }

        fixedPool.shutdown();
        try {
            fixedPool.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Fixed pool interrupted");
        }

        // 2. Cached Thread Pool
        System.out.println("\n2. Cached Thread Pool Demo:");
        ExecutorService cachedPool = Executors.newCachedThreadPool();

        for (int i = 1; i <= 4; i++) {
            cachedPool.submit(new Task("CachedPool-Task-" + i, 1));
        }

        cachedPool.shutdown();
        try {
            cachedPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Cached pool interrupted");
        }

        // 3. Single Thread Executor
        System.out.println("\n3. Single Thread Executor Demo:");
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 3; i++) {
            singleExecutor.submit(new Task("SingleThread-Task-" + i, 1));
        }

        singleExecutor.shutdown();
        try {
            singleExecutor.awaitTermination(8, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Single executor interrupted");
        }

        // 4. Scheduled Thread Pool
        System.out.println("\n4. Scheduled Thread Pool Demo:");
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

        // Schedule tasks with delays
        scheduledPool.schedule(new Task("Delayed-Task-1", 1), 2, TimeUnit.SECONDS);
        scheduledPool.schedule(new Task("Delayed-Task-2", 1), 3, TimeUnit.SECONDS);

        // Schedule recurring task
        ScheduledFuture<?> recurringTask = scheduledPool.scheduleAtFixedRate(
            () -> System.out.println("Recurring task executed at: " + System.currentTimeMillis()),
            1, 2, TimeUnit.SECONDS
        );

        // Cancel recurring task after some time
        scheduledPool.schedule(() -> {
            recurringTask.cancel(false);
            System.out.println("Recurring task cancelled");
        }, 8, TimeUnit.SECONDS);

        scheduledPool.shutdown();
        try {
            scheduledPool.awaitTermination(12, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Scheduled pool interrupted");
        }

        // 5. Future and Callable Demo
        System.out.println("\n5. Future and Callable Demo:");
        ExecutorService callableExecutor = Executors.newFixedThreadPool(2);

        try {
            Future<String> future1 = callableExecutor.submit(new CallableTask("Calculation-1", 2));
            Future<String> future2 = callableExecutor.submit(new CallableTask("Calculation-2", 3));

            System.out.println("Waiting for results...");
            System.out.println("Result 1: " + future1.get()); // Blocking call
            System.out.println("Result 2: " + future2.get()); // Blocking call

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error getting results: " + e.getMessage());
        }

        callableExecutor.shutdown();
        try {
            callableExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Callable executor interrupted");
        }

        System.out.println("\nAll executor framework demos completed!");
    }
}
