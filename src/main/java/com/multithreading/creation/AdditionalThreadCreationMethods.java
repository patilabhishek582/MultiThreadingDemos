package main.java.com.multithreading.creation;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo: Additional thread creation methods - Anonymous classes, Lambda expressions, Custom thread pools
 */
public class AdditionalThreadCreationMethods {

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("=== Additional Thread Creation Methods ===");

        // 1. Anonymous Runnable class
        System.out.println("\n1. Anonymous Runnable Class:");
        Thread anonymousThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous Runnable running on: " + Thread.currentThread().getName());
                for (int i = 1; i <= 3; i++) {
                    try {
                        System.out.println("Anonymous task - iteration: " + i);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Anonymous thread interrupted");
                        return;
                    }
                }
            }
        }, "AnonymousThread");

        anonymousThread.start();

        // 2. Lambda expression
        System.out.println("\n2. Lambda Expression:");
        Thread lambdaThread = new Thread(() -> {
            System.out.println("Lambda expression running on: " + Thread.currentThread().getName());
            for (int i = 1; i <= 3; i++) {
                try {
                    System.out.println("Lambda task - iteration: " + i);
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    System.out.println("Lambda thread interrupted");
                    return;
                }
            }
        }, "LambdaThread");

        lambdaThread.start();

        // 3. Custom ThreadPoolExecutor
        System.out.println("\n3. Custom ThreadPoolExecutor:");
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
            2,                      // corePoolSize
            4,                      // maximumPoolSize
            60L,                    // keepAliveTime
            TimeUnit.SECONDS,       // time unit
            new ArrayBlockingQueue<>(10), // work queue
            new ThreadFactory() {   // custom thread factory
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "CustomPool-Thread-" + taskCounter.incrementAndGet());
                    t.setDaemon(false);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );

        // Submit tasks to custom pool
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            customPool.submit(() -> {
                System.out.println("Custom pool task " + taskId + " running on: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                    System.out.println("Custom pool task " + taskId + " completed");
                } catch (InterruptedException e) {
                    System.out.println("Custom pool task " + taskId + " interrupted");
                }
            });
        }

        // 4. ForkJoinPool for parallel processing
        System.out.println("\n4. ForkJoinPool:");
        ForkJoinPool forkJoinPool = new ForkJoinPool(3);

        // Recursive task for calculating sum
        class SumTask extends RecursiveTask<Long> {
            private final long[] array;
            private final int start;
            private final int end;
            private static final int THRESHOLD = 1000;

            public SumTask(long[] array, int start, int end) {
                this.array = array;
                this.start = start;
                this.end = end;
            }

            @Override
            protected Long compute() {
                if (end - start <= THRESHOLD) {
                    // Base case: compute directly
                    long sum = 0;
                    for (int i = start; i < end; i++) {
                        sum += array[i];
                    }
                    System.out.println("Computing sum from " + start + " to " + end + " on: " + Thread.currentThread().getName());
                    return sum;
                } else {
                    // Divide and conquer
                    int mid = start + (end - start) / 2;
                    SumTask leftTask = new SumTask(array, start, mid);
                    SumTask rightTask = new SumTask(array, mid, end);

                    leftTask.fork(); // Execute left task asynchronously
                    long rightResult = rightTask.compute(); // Compute right task in current thread
                    long leftResult = leftTask.join(); // Wait for left task to complete

                    return leftResult + rightResult;
                }
            }
        }

        // Create a large array for demonstration
        long[] numbers = new long[5000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        SumTask sumTask = new SumTask(numbers, 0, numbers.length);
        Long result = forkJoinPool.invoke(sumTask);
        System.out.println("ForkJoinPool result - Sum: " + result);

        // 5. Virtual Threads (Java 19+) - commented out for compatibility
        System.out.println("\n5. Virtual Threads (Java 19+ feature):");
        System.out.println("Note: Virtual threads require Java 19+ with --enable-preview flag");

        // Uncomment if running on Java 19+
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("Virtual thread task " + taskId + " running on: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                        System.out.println("Virtual thread task " + taskId + " completed");
                    } catch (InterruptedException e) {
                        System.out.println("Virtual thread task " + taskId + " interrupted");
                    }
                });
            }
        }

        // Wait for all threads to complete
        try {
            anonymousThread.join();
            lambdaThread.join();

            customPool.shutdown();
            customPool.awaitTermination(10, TimeUnit.SECONDS);

            forkJoinPool.shutdown();
            forkJoinPool.awaitTermination(10, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

        System.out.println("\nAll additional thread creation demos completed!");
    }
}
