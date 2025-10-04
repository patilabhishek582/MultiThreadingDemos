package main.java.com.multithreading.communication;

import java.util.concurrent.*;

/**
 * Demo: Thread communication using BlockingQueue, CountDownLatch, CyclicBarrier, and Exchanger
 */
public class AdvancedThreadCommunication {

    // Demo 1: Using CountDownLatch for coordination
    static class CountDownLatchDemo {
        public static void demonstrate() {
            System.out.println("=== CountDownLatch Demo ===");
            int numWorkers = 3;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch doneSignal = new CountDownLatch(numWorkers);

            // Create worker threads
            for (int i = 1; i <= numWorkers; i++) {
                final int workerId = i;
                new Thread(() -> {
                    try {
                        System.out.println("Worker " + workerId + " waiting for start signal");
                        startSignal.await(); // Wait for start signal

                        // Do work
                        System.out.println("Worker " + workerId + " started working");
                        Thread.sleep(2000 + (workerId * 500)); // Simulate work
                        System.out.println("Worker " + workerId + " finished work");

                        doneSignal.countDown(); // Signal completion
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }

            try {
                Thread.sleep(1000);
                System.out.println("Main thread: Starting all workers");
                startSignal.countDown(); // Start all workers

                System.out.println("Main thread: Waiting for all workers to complete");
                doneSignal.await(); // Wait for all workers to finish
                System.out.println("Main thread: All workers completed");

            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
            }
        }
    }

    // Demo 2: Using CyclicBarrier for synchronization points
    static class CyclicBarrierDemo {
        public static void demonstrate() {
            System.out.println("\n=== CyclicBarrier Demo ===");
            int numThreads = 4;
            CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
                System.out.println("*** All threads reached the barrier! Continuing together ***");
            });

            for (int i = 1; i <= numThreads; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        // Phase 1
                        System.out.println("Thread " + threadId + " doing phase 1 work");
                        Thread.sleep(1000 + (threadId * 300));
                        System.out.println("Thread " + threadId + " finished phase 1, waiting at barrier");
                        barrier.await();

                        // Phase 2
                        System.out.println("Thread " + threadId + " doing phase 2 work");
                        Thread.sleep(800 + (threadId * 200));
                        System.out.println("Thread " + threadId + " finished phase 2, waiting at barrier");
                        barrier.await();

                        System.out.println("Thread " + threadId + " completed all phases");

                    } catch (InterruptedException | BrokenBarrierException e) {
                        System.out.println("Thread " + threadId + " interrupted");
                    }
                }).start();
            }

            try {
                Thread.sleep(8000); // Wait for demo to complete
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
            }
        }
    }

    // Demo 3: Using Exchanger for bidirectional data exchange
    static class ExchangerDemo {
        public static void demonstrate() {
            System.out.println("\n=== Exchanger Demo ===");
            Exchanger<String> exchanger = new Exchanger<>();

            // Producer thread
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 3; i++) {
                        String data = "Data-" + i;
                        System.out.println("Producer: Producing " + data);
                        Thread.sleep(1000);

                        String received = exchanger.exchange(data);
                        System.out.println("Producer: Received acknowledgment: " + received);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Producer interrupted");
                }
            });

            // Consumer thread
            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 1; i <= 3; i++) {
                        String received = exchanger.exchange("ACK-" + i);
                        System.out.println("Consumer: Received " + received);

                        // Process data
                        Thread.sleep(1500);
                        System.out.println("Consumer: Processed " + received);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Consumer interrupted");
                }
            });

            producer.start();
            consumer.start();

            try {
                producer.join();
                consumer.join();
            } catch (InterruptedException e) {
                System.out.println("Exchanger demo interrupted");
            }
        }
    }

    // Demo 4: Using Phaser for multi-phase coordination
    static class PhaserDemo {
        public static void demonstrate() {
            System.out.println("\n=== Phaser Demo ===");
            Phaser phaser = new Phaser(1); // Register main thread

            for (int i = 1; i <= 3; i++) {
                final int taskId = i;
                phaser.register(); // Register each worker

                new Thread(() -> {
                    try {
                        // Phase 0: Initialization
                        System.out.println("Task " + taskId + " initializing");
                        Thread.sleep(1000);
                        phaser.arriveAndAwaitAdvance(); // Wait for all to complete phase 0

                        // Phase 1: Processing
                        System.out.println("Task " + taskId + " processing");
                        Thread.sleep(1500);
                        phaser.arriveAndAwaitAdvance(); // Wait for all to complete phase 1

                        // Phase 2: Cleanup
                        System.out.println("Task " + taskId + " cleaning up");
                        Thread.sleep(800);
                        phaser.arriveAndDeregister(); // Complete and deregister

                    } catch (InterruptedException e) {
                        System.out.println("Task " + taskId + " interrupted");
                    }
                }).start();
            }

            // Main thread participates in coordination
            phaser.arriveAndAwaitAdvance(); // Wait for phase 0
            System.out.println("Main: All tasks completed initialization");

            phaser.arriveAndAwaitAdvance(); // Wait for phase 1
            System.out.println("Main: All tasks completed processing");

            phaser.arriveAndDeregister(); // Main thread done
            System.out.println("Main: All phases completed");
        }
    }

    // Demo 5: Using CompletionService for collecting results
    static class CompletionServiceDemo {
        public static void demonstrate() {
            System.out.println("\n=== CompletionService Demo ===");
            ExecutorService executor = Executors.newFixedThreadPool(3);
            CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

            // Submit tasks
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                completionService.submit(() -> {
                    int processingTime = (int) (Math.random() * 3000 + 1000);
                    Thread.sleep(processingTime);
                    return "Result from Task " + taskId + " (processed in " + processingTime + "ms)";
                });
            }

            // Collect results as they complete
            try {
                for (int i = 1; i <= 5; i++) {
                    Future<String> result = completionService.take(); // Blocks until result available
                    System.out.println("Received: " + result.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error collecting results: " + e.getMessage());
            }

            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Executor shutdown interrupted");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Advanced Thread Communication Patterns ===");

        CountDownLatchDemo.demonstrate();
        CyclicBarrierDemo.demonstrate();
        ExchangerDemo.demonstrate();
        PhaserDemo.demonstrate();
        CompletionServiceDemo.demonstrate();

        System.out.println("\nAdvanced thread communication demos completed!");
    }
}
