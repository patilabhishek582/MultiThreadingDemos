package main.java.com.multithreading.problems;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 2: Deadlock Problem - Demonstration and Prevention
 */
public class DeadlockProblem {

    // Shared resources
    static class Resource {
        private String name;

        public Resource(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void use(String threadName) {
            System.out.println(threadName + " is using " + name);
            try {
                Thread.sleep(1000); // Simulate resource usage
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Demo 1: Creating a deadlock scenario
    static class DeadlockDemo {
        private static Resource resource1 = new Resource("Resource-1");
        private static Resource resource2 = new Resource("Resource-2");

        static class Worker1 extends Thread {
            @Override
            public void run() {
                synchronized (resource1) {
                    System.out.println("Worker1: Acquired Resource-1");
                    resource1.use("Worker1");

                    System.out.println("Worker1: Waiting for Resource-2...");
                    synchronized (resource2) {
                        System.out.println("Worker1: Acquired Resource-2");
                        resource2.use("Worker1");
                    }
                }
                System.out.println("Worker1: Released all resources");
            }
        }

        static class Worker2 extends Thread {
            @Override
            public void run() {
                synchronized (resource2) {
                    System.out.println("Worker2: Acquired Resource-2");
                    resource2.use("Worker2");

                    System.out.println("Worker2: Waiting for Resource-1...");
                    synchronized (resource1) {
                        System.out.println("Worker2: Acquired Resource-1");
                        resource1.use("Worker2");
                    }
                }
                System.out.println("Worker2: Released all resources");
            }
        }

        public static void demonstrateDeadlock() {
            System.out.println("=== Deadlock Demonstration ===");
            System.out.println("Starting threads that will deadlock...");

            Worker1 worker1 = new Worker1();
            Worker2 worker2 = new Worker2();

            worker1.start();
            worker2.start();

            try {
                // Wait for a reasonable time
                worker1.join(5000);
                worker2.join(5000);

                if (worker1.isAlive() || worker2.isAlive()) {
                    System.out.println("DEADLOCK DETECTED! Threads are still running after 5 seconds.");
                    System.out.println("Worker1 alive: " + worker1.isAlive());
                    System.out.println("Worker2 alive: " + worker2.isAlive());

                    // Force interrupt to break deadlock
                    worker1.interrupt();
                    worker2.interrupt();
                }
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
            }
        }
    }

    // Demo 2: Deadlock prevention using ordered locking
    static class DeadlockPrevention {
        private static Resource resource1 = new Resource("Resource-A");
        private static Resource resource2 = new Resource("Resource-B");

        // Assign unique IDs to resources for ordering
        private static final int RESOURCE1_ID = 1;
        private static final int RESOURCE2_ID = 2;

        static void acquireResourcesInOrder(Resource first, Resource second, String threadName) {
            synchronized (first) {
                System.out.println(threadName + ": Acquired " + first.getName());
                first.use(threadName);

                synchronized (second) {
                    System.out.println(threadName + ": Acquired " + second.getName());
                    second.use(threadName);
                }
                System.out.println(threadName + ": Released " + second.getName());
            }
            System.out.println(threadName + ": Released " + first.getName());
        }

        static class SafeWorker1 extends Thread {
            @Override
            public void run() {
                // Always acquire resources in the same order (resource1 first, then resource2)
                acquireResourcesInOrder(resource1, resource2, "SafeWorker1");
            }
        }

        static class SafeWorker2 extends Thread {
            @Override
            public void run() {
                // Same order as Worker1 (resource1 first, then resource2)
                acquireResourcesInOrder(resource1, resource2, "SafeWorker2");
            }
        }

        public static void demonstratePreventionByOrdering() {
            System.out.println("\n=== Deadlock Prevention by Ordered Locking ===");

            SafeWorker1 worker1 = new SafeWorker1();
            SafeWorker2 worker2 = new SafeWorker2();

            worker1.start();
            worker2.start();

            try {
                worker1.join();
                worker2.join();
                System.out.println("Both workers completed successfully - No deadlock!");
            } catch (InterruptedException e) {
                System.out.println("Safe workers interrupted");
            }
        }
    }

    // Demo 3: Deadlock prevention using timeout
    static class TimeoutBasedPrevention {
        private static final Lock lock1 = new ReentrantLock();
        private static final Lock lock2 = new ReentrantLock();
        private static Resource resource1 = new Resource("Timeout-Resource-1");
        private static Resource resource2 = new Resource("Timeout-Resource-2");

        static class TimeoutWorker extends Thread {
            private Lock firstLock, secondLock;
            private Resource firstResource, secondResource;
            private String workerName;

            public TimeoutWorker(Lock firstLock, Lock secondLock,
                               Resource firstResource, Resource secondResource,
                               String workerName) {
                this.firstLock = firstLock;
                this.secondLock = secondLock;
                this.firstResource = firstResource;
                this.secondResource = secondResource;
                this.workerName = workerName;
            }

            @Override
            public void run() {
                try {
                    if (firstLock.tryLock(2, TimeUnit.SECONDS)) {
                        try {
                            System.out.println(workerName + ": Acquired " + firstResource.getName());
                            firstResource.use(workerName);

                            if (secondLock.tryLock(2, TimeUnit.SECONDS)) {
                                try {
                                    System.out.println(workerName + ": Acquired " + secondResource.getName());
                                    secondResource.use(workerName);
                                } finally {
                                    secondLock.unlock();
                                    System.out.println(workerName + ": Released " + secondResource.getName());
                                }
                            } else {
                                System.out.println(workerName + ": Could not acquire " + secondResource.getName() + " within timeout");
                            }
                        } finally {
                            firstLock.unlock();
                            System.out.println(workerName + ": Released " + firstResource.getName());
                        }
                    } else {
                        System.out.println(workerName + ": Could not acquire " + firstResource.getName() + " within timeout");
                    }
                } catch (InterruptedException e) {
                    System.out.println(workerName + ": Interrupted");
                }
            }
        }

        public static void demonstrateTimeoutPrevention() {
            System.out.println("\n=== Deadlock Prevention using Timeout ===");

            TimeoutWorker worker1 = new TimeoutWorker(lock1, lock2, resource1, resource2, "TimeoutWorker1");
            TimeoutWorker worker2 = new TimeoutWorker(lock2, lock1, resource2, resource1, "TimeoutWorker2");

            worker1.start();
            worker2.start();

            try {
                worker1.join();
                worker2.join();
                System.out.println("Timeout-based workers completed - Deadlock avoided using timeouts!");
            } catch (InterruptedException e) {
                System.out.println("Timeout workers interrupted");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Deadlock Problem Demonstrations ===");

        // Demonstrate deadlock
        DeadlockDemo.demonstrateDeadlock();

        // Show prevention techniques
        DeadlockPrevention.demonstratePreventionByOrdering();
        TimeoutBasedPrevention.demonstrateTimeoutPrevention();

        System.out.println("\nDeadlock problem demos completed!");
    }
}
