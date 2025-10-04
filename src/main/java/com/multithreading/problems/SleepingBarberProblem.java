package main.java.com.multithreading.problems;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 5: Sleeping Barber Problem - Classic producer-consumer variant
 */
public class SleepingBarberProblem {

    // Implementation 1: Using Semaphores
    static class SemaphoreBarberShop {
        private final int CHAIRS;
        private Semaphore customers;      // Number of customers waiting
        private Semaphore barber;         // Barber availability
        private Semaphore mutex;          // Mutex for critical section
        private int waitingCustomers;     // Number of customers waiting
        private AtomicInteger customerCount = new AtomicInteger(0);

        public SemaphoreBarberShop(int chairs) {
            this.CHAIRS = chairs;
            this.customers = new Semaphore(0);
            this.barber = new Semaphore(0);
            this.mutex = new Semaphore(1);
            this.waitingCustomers = 0;
        }

        class Barber extends Thread {
            @Override
            public void run() {
                while (true) {
                    try {
                        customers.acquire(); // Wait for customers
                        mutex.acquire();     // Enter critical section

                        waitingCustomers--;
                        System.out.println("Barber: Customer called in. Waiting customers: " + waitingCustomers);
                        barber.release();    // Ready to cut hair
                        mutex.release();     // Exit critical section

                        cutHair();

                    } catch (InterruptedException e) {
                        System.out.println("Barber interrupted");
                        break;
                    }
                }
            }

            private void cutHair() throws InterruptedException {
                System.out.println("Barber: Cutting hair...");
                Thread.sleep(3000); // Time to cut hair
                System.out.println("Barber: Finished cutting hair");
            }
        }

        class Customer extends Thread {
            private int customerId;

            public Customer(int id) {
                this.customerId = id;
            }

            @Override
            public void run() {
                try {
                    mutex.acquire(); // Enter critical section

                    if (waitingCustomers < CHAIRS) {
                        waitingCustomers++;
                        System.out.println("Customer " + customerId + ": Taking a seat. Waiting customers: " + waitingCustomers);
                        customers.release(); // Signal barber
                        mutex.release();     // Exit critical section

                        barber.acquire();    // Wait for barber
                        getHaircut();

                    } else {
                        System.out.println("Customer " + customerId + ": No chairs available, leaving");
                        mutex.release(); // Exit critical section
                    }

                } catch (InterruptedException e) {
                    System.out.println("Customer " + customerId + " interrupted");
                }
            }

            private void getHaircut() throws InterruptedException {
                System.out.println("Customer " + customerId + ": Getting haircut");
                Thread.sleep(3000); // Time for haircut
                System.out.println("Customer " + customerId + ": Haircut done, leaving");
            }
        }

        public void startBarberShop() {
            System.out.println("=== Semaphore-based Barber Shop ===");
            System.out.println("Barber shop with " + CHAIRS + " waiting chairs");

            Barber barber = new Barber();
            barber.setDaemon(true); // Daemon thread so it doesn't prevent program exit
            barber.start();

            // Generate customers at random intervals
            for (int i = 1; i <= 8; i++) {
                Customer customer = new Customer(i);
                customer.start();

                try {
                    Thread.sleep((long) (Math.random() * 2000 + 500)); // Random arrival time
                } catch (InterruptedException e) {
                    System.out.println("Customer generation interrupted");
                    break;
                }
            }

            // Wait a bit for all customers to finish
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
            }
        }
    }

    // Implementation 2: Using BlockingQueue
    static class QueueBasedBarberShop {
        private final int CHAIRS;
        private BlockingQueue<Integer> waitingChairs;
        private AtomicInteger customerCount = new AtomicInteger(0);
        private volatile boolean shopOpen = true;

        public QueueBasedBarberShop(int chairs) {
            this.CHAIRS = chairs;
            this.waitingChairs = new LinkedBlockingQueue<>(chairs);
        }

        class Barber extends Thread {
            @Override
            public void run() {
                while (shopOpen) {
                    try {
                        Integer customerId = waitingChairs.take(); // Wait for customer
                        if (customerId == -1) break; // Shop closing signal

                        System.out.println("Queue Barber: Serving customer " + customerId);
                        Thread.sleep(3000); // Cut hair
                        System.out.println("Queue Barber: Finished serving customer " + customerId);

                    } catch (InterruptedException e) {
                        System.out.println("Queue Barber interrupted");
                        break;
                    }
                }
                System.out.println("Queue Barber: Shop closed");
            }
        }

        class Customer extends Thread {
            private int customerId;

            public Customer(int id) {
                this.customerId = id;
            }

            @Override
            public void run() {
                try {
                    if (waitingChairs.offer(customerId)) {
                        System.out.println("Queue Customer " + customerId + ": Took a seat. Queue size: " + waitingChairs.size());
                    } else {
                        System.out.println("Queue Customer " + customerId + ": No seats available, leaving");
                    }
                } catch (Exception e) {
                    System.out.println("Queue Customer " + customerId + " error: " + e.getMessage());
                }
            }
        }

        public void startBarberShop() {
            System.out.println("\n=== Queue-based Barber Shop ===");
            System.out.println("Queue-based barber shop with " + CHAIRS + " waiting chairs");

            Barber barber = new Barber();
            barber.start();

            // Generate customers
            Customer[] customers = new Customer[10];
            for (int i = 1; i <= 10; i++) {
                customers[i-1] = new Customer(i);
                customers[i-1].start();

                try {
                    Thread.sleep((long) (Math.random() * 1500 + 300));
                } catch (InterruptedException e) {
                    System.out.println("Customer generation interrupted");
                    break;
                }
            }

            // Wait for all customers to arrive
            try {
                for (Customer customer : customers) {
                    customer.join();
                }

                // Wait for barber to finish all customers
                Thread.sleep(5000);

                // Close shop
                shopOpen = false;
                waitingChairs.put(-1); // Signal barber to stop
                barber.join();

            } catch (InterruptedException e) {
                System.out.println("Shop closing interrupted");
            }
        }
    }

    // Implementation 3: Monitor-based solution
    static class MonitorBasedBarberShop {
        private final int CHAIRS;
        private int waitingCustomers = 0;
        private boolean barberSleeping = true;
        private final Object monitor = new Object();

        public MonitorBasedBarberShop(int chairs) {
            this.CHAIRS = chairs;
        }

        class Barber extends Thread {
            @Override
            public void run() {
                while (true) {
                    synchronized (monitor) {
                        while (waitingCustomers == 0) {
                            try {
                                barberSleeping = true;
                                System.out.println("Monitor Barber: Going to sleep");
                                monitor.wait(); // Sleep until customer arrives
                            } catch (InterruptedException e) {
                                System.out.println("Monitor Barber interrupted");
                                return;
                            }
                        }

                        barberSleeping = false;
                        waitingCustomers--;
                        System.out.println("Monitor Barber: Woke up, serving customer. Waiting: " + waitingCustomers);
                        monitor.notifyAll(); // Wake up waiting customer
                    }

                    try {
                        cutHair();
                    } catch (InterruptedException e) {
                        System.out.println("Monitor Barber interrupted during haircut");
                        return;
                    }
                }
            }

            private void cutHair() throws InterruptedException {
                System.out.println("Monitor Barber: Cutting hair...");
                Thread.sleep(2500);
                System.out.println("Monitor Barber: Haircut finished");
            }
        }

        class Customer extends Thread {
            private int customerId;

            public Customer(int id) {
                this.customerId = id;
            }

            @Override
            public void run() {
                synchronized (monitor) {
                    if (waitingCustomers < CHAIRS) {
                        waitingCustomers++;
                        System.out.println("Monitor Customer " + customerId + ": Taking seat. Waiting: " + waitingCustomers);

                        if (barberSleeping) {
                            monitor.notify(); // Wake up barber
                        }

                        try {
                            monitor.wait(); // Wait for turn
                            System.out.println("Monitor Customer " + customerId + ": Getting haircut");
                        } catch (InterruptedException e) {
                            System.out.println("Monitor Customer " + customerId + " interrupted");
                        }
                    } else {
                        System.out.println("Monitor Customer " + customerId + ": Shop full, leaving");
                    }
                }
            }
        }

        public void startBarberShop() {
            System.out.println("\n=== Monitor-based Barber Shop ===");
            System.out.println("Monitor-based barber shop with " + CHAIRS + " waiting chairs");

            Barber barber = new Barber();
            barber.setDaemon(true);
            barber.start();

            // Generate customers
            for (int i = 1; i <= 7; i++) {
                Customer customer = new Customer(i);
                customer.start();

                try {
                    Thread.sleep((long) (Math.random() * 2000 + 800));
                } catch (InterruptedException e) {
                    System.out.println("Customer generation interrupted");
                    break;
                }
            }

            try {
                Thread.sleep(12000); // Let the shop run
            } catch (InterruptedException e) {
                System.out.println("Shop operation interrupted");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Sleeping Barber Problem Demonstrations ===");

        // Demo 1: Semaphore-based solution
        SemaphoreBarberShop semShop = new SemaphoreBarberShop(3);
        semShop.startBarberShop();

        // Demo 2: Queue-based solution
        QueueBasedBarberShop queueShop = new QueueBasedBarberShop(4);
        queueShop.startBarberShop();

        // Demo 3: Monitor-based solution
        MonitorBasedBarberShop monitorShop = new MonitorBasedBarberShop(2);
        monitorShop.startBarberShop();

        System.out.println("\nSleeping Barber problem demos completed!");
    }
}
