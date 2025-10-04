package main.java.com.multithreading.communication;

/**
 * Demo: Basic thread communication using wait() and notify()
 */
public class WaitNotifyDemo {

    static class SharedResource {
        private String message;
        private boolean messageAvailable = false;

        public synchronized void setMessage(String message) {
            while (messageAvailable) {
                try {
                    wait(); // Wait until message is consumed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            this.message = message;
            this.messageAvailable = true;
            System.out.println("Producer set message: " + message);
            notify(); // Notify waiting consumer
        }

        public synchronized String getMessage() {
            while (!messageAvailable) {
                try {
                    wait(); // Wait until message is available
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            String msg = this.message;
            this.messageAvailable = false;
            System.out.println("Consumer got message: " + msg);
            notify(); // Notify waiting producer
            return msg;
        }
    }

    static class Producer extends Thread {
        private SharedResource resource;
        private String[] messages;

        public Producer(SharedResource resource, String[] messages) {
            this.resource = resource;
            this.messages = messages;
        }

        @Override
        public void run() {
            for (String message : messages) {
                resource.setMessage(message);
                try {
                    Thread.sleep(1000); // Simulate processing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            resource.setMessage("END"); // Signal end of messages
        }
    }

    static class Consumer extends Thread {
        private SharedResource resource;

        public Consumer(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            String message;
            while (!(message = resource.getMessage()).equals("END")) {
                // Process the message
                System.out.println("Processing: " + message);
                try {
                    Thread.sleep(1500); // Simulate processing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Consumer finished processing all messages");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Wait/Notify Communication Demo ===");

        SharedResource sharedResource = new SharedResource();
        String[] messages = {"Hello", "World", "Java", "Threading", "Communication"};

        Producer producer = new Producer(sharedResource, messages);
        Consumer consumer = new Consumer(sharedResource);

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

        System.out.println("Wait/Notify demo completed!");
    }
}
