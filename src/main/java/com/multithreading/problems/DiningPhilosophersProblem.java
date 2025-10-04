package main.java.com.multithreading.problems;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 4: Dining Philosophers Problem - Classic synchronization problem
 */
public class DiningPhilosophersProblem {

    // Implementation 1: Using Semaphores (avoiding deadlock)
    static class SemaphorePhilosophers {
        private static final int NUM_PHILOSOPHERS = 5;
        private Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];
        private Semaphore maxDiners = new Semaphore(NUM_PHILOSOPHERS - 1); // Prevent deadlock

        public SemaphorePhilosophers() {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new Semaphore(1); // Binary semaphore for each fork
            }
        }

        class Philosopher extends Thread {
            private int id;
            private int leftFork;
            private int rightFork;

            public Philosopher(int id) {
                this.id = id;
                this.leftFork = id;
                this.rightFork = (id + 1) % NUM_PHILOSOPHERS;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 3; i++) { // Each philosopher eats 3 times
                        think();
                        eat();
                    }
                    System.out.println("Philosopher " + id + " finished dining");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            private void think() throws InterruptedException {
                System.out.println("Philosopher " + id + " is thinking...");
                Thread.sleep((long) (Math.random() * 2000));
            }

            private void eat() throws InterruptedException {
                maxDiners.acquire(); // Limit concurrent diners to prevent deadlock

                forks[leftFork].acquire();
                System.out.println("Philosopher " + id + " picked up left fork " + leftFork);

                forks[rightFork].acquire();
                System.out.println("Philosopher " + id + " picked up right fork " + rightFork);

                System.out.println("Philosopher " + id + " is eating...");
                Thread.sleep((long) (Math.random() * 2000));

                forks[rightFork].release();
                System.out.println("Philosopher " + id + " put down right fork " + rightFork);

                forks[leftFork].release();
                System.out.println("Philosopher " + id + " put down left fork " + leftFork);

                maxDiners.release();
            }
        }

        public void startDining() {
            System.out.println("=== Semaphore-based Dining Philosophers ===");
            Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                philosophers[i] = new Philosopher(i);
                philosophers[i].start();
            }

            try {
                for (Philosopher philosopher : philosophers) {
                    philosopher.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Dining interrupted");
            }
        }
    }

    // Implementation 2: Using Locks with ordering (asymmetric solution)
    static class LockBasedPhilosophers {
        private static final int NUM_PHILOSOPHERS = 5;
        private Lock[] forks = new Lock[NUM_PHILOSOPHERS];

        public LockBasedPhilosophers() {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new ReentrantLock();
            }
        }

        class Philosopher extends Thread {
            private int id;
            private Lock firstFork;
            private Lock secondFork;

            public Philosopher(int id) {
                this.id = id;
                int leftFork = id;
                int rightFork = (id + 1) % NUM_PHILOSOPHERS;

                // Asymmetric solution: order locks by their index to prevent deadlock
                if (leftFork < rightFork) {
                    firstFork = forks[leftFork];
                    secondFork = forks[rightFork];
                } else {
                    firstFork = forks[rightFork];
                    secondFork = forks[leftFork];
                }
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 3; i++) {
                        think();
                        eat();
                    }
                    System.out.println("Lock-based Philosopher " + id + " finished dining");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            private void think() throws InterruptedException {
                System.out.println("Lock-based Philosopher " + id + " is thinking...");
                Thread.sleep((long) (Math.random() * 1500));
            }

            private void eat() throws InterruptedException {
                firstFork.lock();
                try {
                    System.out.println("Lock-based Philosopher " + id + " picked up first fork");

                    secondFork.lock();
                    try {
                        System.out.println("Lock-based Philosopher " + id + " picked up second fork");
                        System.out.println("Lock-based Philosopher " + id + " is eating...");
                        Thread.sleep((long) (Math.random() * 1500));
                        System.out.println("Lock-based Philosopher " + id + " finished eating");
                    } finally {
                        secondFork.unlock();
                        System.out.println("Lock-based Philosopher " + id + " put down second fork");
                    }
                } finally {
                    firstFork.unlock();
                    System.out.println("Lock-based Philosopher " + id + " put down first fork");
                }
            }
        }

        public void startDining() {
            System.out.println("\n=== Lock-based Dining Philosophers (Asymmetric Solution) ===");
            Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                philosophers[i] = new Philosopher(i);
                philosophers[i].start();
            }

            try {
                for (Philosopher philosopher : philosophers) {
                    philosopher.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Lock-based dining interrupted");
            }
        }
    }

    // Implementation 3: Monitor-based solution using synchronized
    static class MonitorBasedPhilosophers {
        private static final int NUM_PHILOSOPHERS = 5;
        private enum State { THINKING, HUNGRY, EATING }
        private State[] states = new State[NUM_PHILOSOPHERS];
        private Object[] monitors = new Object[NUM_PHILOSOPHERS];

        public MonitorBasedPhilosophers() {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                states[i] = State.THINKING;
                monitors[i] = new Object();
            }
        }

        private int leftNeighbor(int i) {
            return (i + NUM_PHILOSOPHERS - 1) % NUM_PHILOSOPHERS;
        }

        private int rightNeighbor(int i) {
            return (i + 1) % NUM_PHILOSOPHERS;
        }

        private void test(int i) {
            if (states[i] == State.HUNGRY &&
                states[leftNeighbor(i)] != State.EATING &&
                states[rightNeighbor(i)] != State.EATING) {

                states[i] = State.EATING;
                synchronized (monitors[i]) {
                    monitors[i].notify();
                }
            }
        }

        public void pickUpForks(int i) throws InterruptedException {
            synchronized (this) {
                states[i] = State.HUNGRY;
                System.out.println("Monitor Philosopher " + i + " is hungry");
                test(i);
            }

            synchronized (monitors[i]) {
                while (states[i] != State.EATING) {
                    monitors[i].wait();
                }
            }
        }

        public void putDownForks(int i) {
            synchronized (this) {
                states[i] = State.THINKING;
                System.out.println("Monitor Philosopher " + i + " finished eating, now thinking");
                test(leftNeighbor(i));
                test(rightNeighbor(i));
            }
        }

        class Philosopher extends Thread {
            private int id;

            public Philosopher(int id) {
                this.id = id;
            }

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 3; i++) {
                        think();
                        pickUpForks(id);
                        eat();
                        putDownForks(id);
                    }
                    System.out.println("Monitor Philosopher " + id + " finished all meals");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            private void think() throws InterruptedException {
                System.out.println("Monitor Philosopher " + id + " is thinking...");
                Thread.sleep((long) (Math.random() * 1000));
            }

            private void eat() throws InterruptedException {
                System.out.println("Monitor Philosopher " + id + " is eating...");
                Thread.sleep((long) (Math.random() * 1000));
            }
        }

        public void startDining() {
            System.out.println("\n=== Monitor-based Dining Philosophers ===");
            Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                philosophers[i] = new Philosopher(i);
                philosophers[i].start();
            }

            try {
                for (Philosopher philosopher : philosophers) {
                    philosopher.join();
                }
            } catch (InterruptedException e) {
                System.out.println("Monitor-based dining interrupted");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Dining Philosophers Problem Demonstrations ===");

        // Demo 1: Semaphore-based solution
        SemaphorePhilosophers semSolution = new SemaphorePhilosophers();
        semSolution.startDining();

        // Demo 2: Lock-based solution with ordering
        LockBasedPhilosophers lockSolution = new LockBasedPhilosophers();
        lockSolution.startDining();

        // Demo 3: Monitor-based solution
        MonitorBasedPhilosophers monitorSolution = new MonitorBasedPhilosophers();
        monitorSolution.startDining();

        System.out.println("\nDining Philosophers problem demos completed!");
    }
}
