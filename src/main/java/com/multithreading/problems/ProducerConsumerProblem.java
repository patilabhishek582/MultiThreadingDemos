package main.java.com.multithreading.problems;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 1: Producer-Consumer Problem with multiple implementations
 */
public class ProducerConsumerProblem {

    // Implementation 1: Using synchronized methods and wait/notify
    static class SynchronizedBuffer {
        private int[] buffer;
        private int count = 0;
        private int in = 0;  // next position to insert
        private int out = 0; // next position to remove

        public SynchronizedBuffer(int size) {
            buffer = new int[size];
        }

        public synchronized void produce(int item) throws InterruptedException {
            while (count == buffer.length) {
                System.out.println("Buffer full, producer waiting...");
                wait();
            }

            buffer[in] = item;
            in = (in + 1) % buffer.length;
            count++;
            System.out.println("Produced: " + item + " | Buffer count: " + count);
            notifyAll();
        }

        public synchronized int consume() throws InterruptedException {
            while (count == 0) {
                System.out.println("Buffer empty, consumer waiting...");
                wait();
            }

            int item = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            System.out.println("Consumed: " + item + " | Buffer count: " + count);
            notifyAll();
            return item;
        }
    }

    // Implementation 2: Using ReentrantLock and Conditions
    static class LockBasedBuffer {
        private int[] buffer;
        private int count = 0;
        private int in = 0;
        private int out = 0;
        private final Lock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public LockBasedBuffer(int size) {
            buffer = new int[size];
        }

        public void produce(int item) throws InterruptedException {
            lock.lock();
            try {
                while (count == buffer.length) {
                    System.out.println("Lock-based buffer full, producer waiting...");
                    notFull.await();
                }

                buffer[in] = item;
                in = (in + 1) % buffer.length;
                count++;
                System.out.println("Lock-produced: " + item + " | Buffer count: " + count);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public int consume() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    System.out.println("Lock-based buffer empty, consumer waiting...");
                    notEmpty.await();
                }

                int item = buffer[out];
                out = (out + 1) % buffer.length;
                count--;
                System.out.println("Lock-consumed: " + item + " | Buffer count: " + count);
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }
    }

    static class Producer extends Thread {
        private SynchronizedBuffer buffer;
        private LockBasedBuffer lockBuffer;
        private int itemCount;
        private String type;

        public Producer(SynchronizedBuffer buffer, int itemCount, String type) {
            this.buffer = buffer;
            this.itemCount = itemCount;
            this.type = type;
        }

        public Producer(LockBasedBuffer lockBuffer, int itemCount, String type) {
            this.lockBuffer = lockBuffer;
            this.itemCount = itemCount;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= itemCount; i++) {
                    int item = (int) (Math.random() * 100);
                    if (buffer != null) {
                        buffer.produce(item);
                    } else {
                        lockBuffer.produce(item);
                    }
                    Thread.sleep(500); // Simulate production time
                }
                System.out.println(type + " Producer finished");
            } catch (InterruptedException e) {
                System.out.println(type + " Producer interrupted");
            }
        }
    }

    static class Consumer extends Thread {
        private SynchronizedBuffer buffer;
        private LockBasedBuffer lockBuffer;
        private int itemCount;
        private String type;

        public Consumer(SynchronizedBuffer buffer, int itemCount, String type) {
            this.buffer = buffer;
            this.itemCount = itemCount;
            this.type = type;
        }

        public Consumer(LockBasedBuffer lockBuffer, int itemCount, String type) {
            this.lockBuffer = lockBuffer;
            this.itemCount = itemCount;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= itemCount; i++) {
                    if (buffer != null) {
                        buffer.consume();
                    } else {
                        lockBuffer.consume();
                    }
                    Thread.sleep(700); // Simulate consumption time
                }
                System.out.println(type + " Consumer finished");
            } catch (InterruptedException e) {
                System.out.println(type + " Consumer interrupted");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Producer-Consumer Problem Demo ===");

        // Demo 1: Synchronized implementation
        System.out.println("\n1. Synchronized Implementation:");
        SynchronizedBuffer syncBuffer = new SynchronizedBuffer(5);

        Producer syncProducer = new Producer(syncBuffer, 8, "Sync");
        Consumer syncConsumer = new Consumer(syncBuffer, 8, "Sync");

        syncProducer.start();
        syncConsumer.start();

        try {
            syncProducer.join();
            syncConsumer.join();
        } catch (InterruptedException e) {
            System.out.println("Sync demo interrupted");
        }

        // Demo 2: Lock-based implementation
        System.out.println("\n2. Lock-based Implementation:");
        LockBasedBuffer lockBuffer = new LockBasedBuffer(3);

        Producer lockProducer = new Producer(lockBuffer, 6, "Lock");
        Consumer lockConsumer = new Consumer(lockBuffer, 6, "Lock");

        lockProducer.start();
        lockConsumer.start();

        try {
            lockProducer.join();
            lockConsumer.join();
        } catch (InterruptedException e) {
            System.out.println("Lock demo interrupted");
        }

        // Demo 3: Using BlockingQueue (simplest approach)
        System.out.println("\n3. BlockingQueue Implementation:");
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(4);

        Thread blockingProducer = new Thread(() -> {
            try {
                for (int i = 1; i <= 7; i++) {
                    int item = (int) (Math.random() * 100);
                    blockingQueue.put(item); // Blocks if queue is full
                    System.out.println("BlockingQueue produced: " + item + " | Queue size: " + blockingQueue.size());
                    Thread.sleep(400);
                }
                System.out.println("BlockingQueue Producer finished");
            } catch (InterruptedException e) {
                System.out.println("BlockingQueue Producer interrupted");
            }
        });

        Thread blockingConsumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 7; i++) {
                    int item = blockingQueue.take(); // Blocks if queue is empty
                    System.out.println("BlockingQueue consumed: " + item + " | Queue size: " + blockingQueue.size());
                    Thread.sleep(600);
                }
                System.out.println("BlockingQueue Consumer finished");
            } catch (InterruptedException e) {
                System.out.println("BlockingQueue Consumer interrupted");
            }
        });

        blockingProducer.start();
        blockingConsumer.start();

        try {
            blockingProducer.join();
            blockingConsumer.join();
        } catch (InterruptedException e) {
            System.out.println("BlockingQueue demo interrupted");
        }

    }
}
