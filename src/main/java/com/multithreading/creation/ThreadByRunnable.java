package main.java.com.multithreading.creation;

/**
 * Demo: Creating threads by implementing Runnable interface
 */
public class ThreadByRunnable implements Runnable {
    private String taskName;
    private int iterations;

    public ThreadByRunnable(String taskName, int iterations) {
        this.taskName = taskName;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskName + " started on thread: " + Thread.currentThread().getName());

        for (int i = 1; i <= iterations; i++) {
            try {
                System.out.println(taskName + " - Iteration: " + i + " [Thread: " + Thread.currentThread().getName() + "]");
                Thread.sleep(800); // Sleep for 800ms
            } catch (InterruptedException e) {
                System.out.println(taskName + " interrupted");
                return;
            }
        }

        System.out.println("Task " + taskName + " completed");
    }

    public static void main(String[] args) {
        System.out.println("=== Thread Creation by Implementing Runnable Interface ===");

        // Create tasks
        ThreadByRunnable task1 = new ThreadByRunnable("DataProcessor", 4);
        ThreadByRunnable task2 = new ThreadByRunnable("FileHandler", 3);
        ThreadByRunnable task3 = new ThreadByRunnable("NetworkService", 5);

        // Create threads with tasks
        Thread thread1 = new Thread(task1, "Thread-DataProcessor");
        Thread thread2 = new Thread(task2, "Thread-FileHandler");
        Thread thread3 = new Thread(task3, "Thread-NetworkService");

        // Start all threads
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            // Wait for all threads to complete
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

        System.out.println("All tasks completed. Main thread exiting.");
    }
}
