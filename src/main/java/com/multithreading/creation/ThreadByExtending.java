package main.java.com.multithreading.creation;

/**
 * Demo: Creating threads by extending Thread class
 */
public class ThreadByExtending extends Thread {
    private String threadName;
    private int iterations;

    public ThreadByExtending(String name, int iterations) {
        this.threadName = name;
        this.iterations = iterations;
        setName(name);
    }

    @Override
    public void run() {
        System.out.println("Thread " + threadName + " started");

        for (int i = 1; i <= iterations; i++) {
            try {
                System.out.println(threadName + " - Iteration: " + i);
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                System.out.println(threadName + " interrupted");
                return;
            }
        }

        System.out.println("Thread " + threadName + " completed");
    }

    public static void main(String[] args) {
        System.out.println("=== Thread Creation by Extending Thread Class ===");

        // Create and start threads
        ThreadByExtending thread1 = new ThreadByExtending("Worker-1", 3);
        ThreadByExtending thread2 = new ThreadByExtending("Worker-2", 3);
        ThreadByExtending thread3 = new ThreadByExtending("Worker-3", 3);

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

        System.out.println("All threads completed. Main thread exiting.");
    }
}
