package main.java.com.multithreading.problems;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem 3: Reader-Writer Problem with multiple implementations
 */
public class ReaderWriterProblem {

    // Shared resource (database/file)
    static class SharedDatabase {
        private String data = "Initial Data";
        private int readCount = 0;

        // Implementation 1: Using ReadWriteLock (simplest and recommended)
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        public void readWithReadWriteLock(String readerName) {
            readLock.lock();
            try {
                System.out.println(readerName + " is reading: " + data);
                Thread.sleep(1000); // Simulate reading time
                System.out.println(readerName + " finished reading");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                readLock.unlock();
            }
        }

        public void writeWithReadWriteLock(String writerName, String newData) {
            writeLock.lock();
            try {
                System.out.println(writerName + " is writing...");
                Thread.sleep(2000); // Simulate writing time
                this.data = newData;
                System.out.println(writerName + " wrote: " + newData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }

        // Implementation 2: Using synchronized methods and counters
        private final Object readCountLock = new Object();
        private final Object writeLock2 = new Object();

        public void readWithSynchronized(String readerName) {
            synchronized (readCountLock) {
                readCount++;
                if (readCount == 1) {
                    synchronized (writeLock2) {
                        // First reader locks writers out
                    }
                }
            }

            try {
                // Reading section
                System.out.println(readerName + " is reading (sync): " + data + " [Readers: " + readCount + "]");
                Thread.sleep(1000);
                System.out.println(readerName + " finished reading (sync)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            synchronized (readCountLock) {
                readCount--;
                if (readCount == 0) {
                    synchronized (writeLock2) {
                        // Last reader releases writers
                    }
                }
            }
        }

        public void writeWithSynchronized(String writerName, String newData) {
            synchronized (writeLock2) {
                try {
                    System.out.println(writerName + " is writing (sync)...");
                    Thread.sleep(2000);
                    this.data = newData;
                    System.out.println(writerName + " wrote (sync): " + newData);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Implementation 3: Using Semaphores
    static class SemaphoreBasedDatabase {
        private String data = "Semaphore Initial Data";
        private int readCount = 0;
        private Semaphore resourceSemaphore = new Semaphore(1); // Binary semaphore for write access
        private Semaphore readCountSemaphore = new Semaphore(1); // Protects readCount

        public void read(String readerName) {
            try {
                readCountSemaphore.acquire();
                readCount++;
                if (readCount == 1) {
                    resourceSemaphore.acquire(); // First reader blocks writers
                }
                readCountSemaphore.release();

                // Reading section
                System.out.println(readerName + " is reading (semaphore): " + data + " [Readers: " + readCount + "]");
                Thread.sleep(1000);
                System.out.println(readerName + " finished reading (semaphore)");

                readCountSemaphore.acquire();
                readCount--;
                if (readCount == 0) {
                    resourceSemaphore.release(); // Last reader unblocks writers
                }
                readCountSemaphore.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void write(String writerName, String newData) {
            try {
                resourceSemaphore.acquire();

                System.out.println(writerName + " is writing (semaphore)...");
                Thread.sleep(2000);
                this.data = newData;
                System.out.println(writerName + " wrote (semaphore): " + newData);

                resourceSemaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Reader extends Thread {
        private SharedDatabase database;
        private SemaphoreBasedDatabase semDatabase;
        private String readerName;
        private int readOperations;
        private String implementationType;

        public Reader(SharedDatabase database, String readerName, int readOperations, String implementationType) {
            this.database = database;
            this.readerName = readerName;
            this.readOperations = readOperations;
            this.implementationType = implementationType;
        }

        public Reader(SemaphoreBasedDatabase semDatabase, String readerName, int readOperations) {
            this.semDatabase = semDatabase;
            this.readerName = readerName;
            this.readOperations = readOperations;
            this.implementationType = "Semaphore";
        }

        @Override
        public void run() {
            for (int i = 1; i <= readOperations; i++) {
                try {
                    if (implementationType.equals("ReadWriteLock")) {
                        database.readWithReadWriteLock(readerName + "-" + i);
                    } else if (implementationType.equals("Synchronized")) {
                        database.readWithSynchronized(readerName + "-" + i);
                    } else if (implementationType.equals("Semaphore")) {
                        semDatabase.read(readerName + "-" + i);
                    }

                    Thread.sleep(500); // Pause between reads
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println(readerName + " (" + implementationType + ") completed all reads");
        }
    }

    static class Writer extends Thread {
        private SharedDatabase database;
        private SemaphoreBasedDatabase semDatabase;
        private String writerName;
        private int writeOperations;
        private String implementationType;

        public Writer(SharedDatabase database, String writerName, int writeOperations, String implementationType) {
            this.database = database;
            this.writerName = writerName;
            this.writeOperations = writeOperations;
            this.implementationType = implementationType;
        }

        public Writer(SemaphoreBasedDatabase semDatabase, String writerName, int writeOperations) {
            this.semDatabase = semDatabase;
            this.writerName = writerName;
            this.writeOperations = writeOperations;
            this.implementationType = "Semaphore";
        }

        @Override
        public void run() {
            for (int i = 1; i <= writeOperations; i++) {
                try {
                    String newData = writerName + "-Data-" + i;

                    if (implementationType.equals("ReadWriteLock")) {
                        database.writeWithReadWriteLock(writerName + "-" + i, newData);
                    } else if (implementationType.equals("Synchronized")) {
                        database.writeWithSynchronized(writerName + "-" + i, newData);
                    } else if (implementationType.equals("Semaphore")) {
                        semDatabase.write(writerName + "-" + i, newData);
                    }

                    Thread.sleep(1000); // Pause between writes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println(writerName + " (" + implementationType + ") completed all writes");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Reader-Writer Problem Demonstrations ===");

        // Demo 1: ReadWriteLock implementation
        System.out.println("\n1. ReadWriteLock Implementation:");
        SharedDatabase database1 = new SharedDatabase();

        Reader reader1 = new Reader(database1, "Reader1", 3, "ReadWriteLock");
        Reader reader2 = new Reader(database1, "Reader2", 2, "ReadWriteLock");
        Writer writer1 = new Writer(database1, "Writer1", 2, "ReadWriteLock");

        reader1.start();
        writer1.start();
        reader2.start();

        try {
            reader1.join();
            reader2.join();
            writer1.join();
        } catch (InterruptedException e) {
            System.out.println("ReadWriteLock demo interrupted");
        }

        // Demo 2: Synchronized implementation
        System.out.println("\n2. Synchronized Implementation:");
        SharedDatabase database2 = new SharedDatabase();

        Reader reader3 = new Reader(database2, "SyncReader1", 2, "Synchronized");
        Reader reader4 = new Reader(database2, "SyncReader2", 2, "Synchronized");
        Writer writer2 = new Writer(database2, "SyncWriter1", 1, "Synchronized");

        reader3.start();
        reader4.start();
        writer2.start();

        try {
            reader3.join();
            reader4.join();
            writer2.join();
        } catch (InterruptedException e) {
            System.out.println("Synchronized demo interrupted");
        }

        // Demo 3: Semaphore implementation
        System.out.println("\n3. Semaphore Implementation:");
        SemaphoreBasedDatabase database3 = new SemaphoreBasedDatabase();

        Reader reader5 = new Reader(database3, "SemReader1", 2);
        Reader reader6 = new Reader(database3, "SemReader2", 2);
        Writer writer3 = new Writer(database3, "SemWriter1", 1);

        reader5.start();
        reader6.start();
        writer3.start();

        try {
            reader5.join();
            reader6.join();
            writer3.join();
        } catch (InterruptedException e) {
            System.out.println("Semaphore demo interrupted");
        }

        System.out.println("\nReader-Writer problem demos completed!");
    }
}
