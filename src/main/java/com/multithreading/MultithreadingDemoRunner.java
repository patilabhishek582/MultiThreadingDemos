package main.java.com.multithreading;

import main.java.com.multithreading.creation.*;
import main.java.com.multithreading.communication.*;
import main.java.com.multithreading.problems.*;

import java.util.Scanner;

public class MultithreadingDemoRunner {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("    JAVA MULTITHREADING DEMO SUITE");
        System.out.println("=========================================");

        while (true) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    runThreadCreationDemos();
                    break;
                case 2:
                    runThreadCommunicationDemos();
                    break;
                case 3:
                    runClassicProblemDemos();
                    break;
                case 4:
                    runAllDemos();
                    break;
                case 0:
                    System.out.println("Exiting demo suite. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void displayMenu() {
        System.out.println("\n=================== MENU ===================");
        System.out.println("1. Thread Creation Methods");
        System.out.println("2. Thread Communication Patterns");
        System.out.println("3. Classic Multithreading Problems");
        System.out.println("4. Run All Demos");
        System.out.println("0. Exit");
        System.out.println("============================================");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void runThreadCreationDemos() {
        System.out.println("\n=== THREAD CREATION METHODS ===");

        while (true) {
            System.out.println("\nThread Creation Options:");
            System.out.println("1. Extending Thread Class");
            System.out.println("2. Implementing Runnable Interface");
            System.out.println("3. Executor Framework");
            System.out.println("4. CompletableFuture");
            System.out.println("5. Additional Methods (Anonymous, Lambda, Custom Pools)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Running Thread Extension Demo ---");
                    ThreadByExtending.main(new String[]{});
                    break;
                case 2:
                    System.out.println("\n--- Running Runnable Interface Demo ---");
                    ThreadByRunnable.main(new String[]{});
                    break;
                case 3:
                    System.out.println("\n--- Running Executor Framework Demo ---");
                    ThreadByExecutorFramework.main(new String[]{});
                    break;
                case 4:
                    System.out.println("\n--- Running CompletableFuture Demo ---");
                    ThreadByCompletableFuture.main(new String[]{});
                    break;
                case 5:
                    System.out.println("\n--- Running Additional Methods Demo ---");
                    AdditionalThreadCreationMethods.main(new String[]{});
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void runThreadCommunicationDemos() {
        System.out.println("\n=== THREAD COMMUNICATION PATTERNS ===");

        while (true) {
            System.out.println("\nCommunication Options:");
            System.out.println("1. Basic Wait/Notify");
            System.out.println("2. Advanced Communication (CountDownLatch, CyclicBarrier, etc.)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Running Wait/Notify Demo ---");
                    WaitNotifyDemo.main(new String[]{});
                    break;
                case 2:
                    System.out.println("\n--- Running Advanced Communication Demo ---");
                    AdvancedThreadCommunication.main(new String[]{});
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void runClassicProblemDemos() {
        System.out.println("\n=== CLASSIC MULTITHREADING PROBLEMS ===");

        while (true) {
            System.out.println("\nProblem Options:");
            System.out.println("1. Producer-Consumer Problem");
            System.out.println("2. Deadlock Problem");
            System.out.println("3. Reader-Writer Problem");
            System.out.println("4. Dining Philosophers Problem");
            System.out.println("5. Sleeping Barber Problem");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = getChoice();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Running Producer-Consumer Demo ---");
                    ProducerConsumerProblem.main(new String[]{});
                    break;
                case 2:
                    System.out.println("\n--- Running Deadlock Demo ---");
                    DeadlockProblem.main(new String[]{});
                    break;
                case 3:
                    System.out.println("\n--- Running Reader-Writer Demo ---");
                    ReaderWriterProblem.main(new String[]{});
                    break;
                case 4:
                    System.out.println("\n--- Running Dining Philosophers Demo ---");
                    DiningPhilosophersProblem.main(new String[]{});
                    break;
                case 5:
                    System.out.println("\n--- Running Sleeping Barber Demo ---");
                    SleepingBarberProblem.main(new String[]{});
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void runAllDemos() {
        System.out.println("\n=== RUNNING ALL DEMOS ===");
        System.out.println("This will run all demonstrations sequentially...");
        System.out.println("Note: Some demos may take several minutes to complete.");
        System.out.print("Continue? (y/n): ");

        String response = scanner.nextLine();
        if (!response.toLowerCase().startsWith("y")) {
            return;
        }

        System.out.println("\n--- Thread Creation Demos ---");
        ThreadByExtending.main(new String[]{});
        ThreadByRunnable.main(new String[]{});
        ThreadByExecutorFramework.main(new String[]{});
        ThreadByCompletableFuture.main(new String[]{});
        AdditionalThreadCreationMethods.main(new String[]{});

        System.out.println("\n--- Communication Demos ---");
        WaitNotifyDemo.main(new String[]{});
        AdvancedThreadCommunication.main(new String[]{});

        System.out.println("\n--- Classic Problem Demos ---");
        ProducerConsumerProblem.main(new String[]{});
        DeadlockProblem.main(new String[]{});
        ReaderWriterProblem.main(new String[]{});
        DiningPhilosophersProblem.main(new String[]{});
        SleepingBarberProblem.main(new String[]{});

        System.out.println("\n=== ALL DEMOS COMPLETED ===");
    }
}
