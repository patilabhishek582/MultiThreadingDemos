package main.java.com.multithreading.creation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demo: Using CompletableFuture for asynchronous programming
 */
public class ThreadByCompletableFuture {

    // Simulate time-consuming operations
    public static String fetchUserData(String userId) {
        try {
            Thread.sleep(2000); // Simulate network delay
            return "UserData for " + userId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error fetching user data";
        }
    }

    public static String fetchUserPreferences(String userId) {
        try {
            Thread.sleep(1500); // Simulate database query
            return "Preferences for " + userId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error fetching preferences";
        }
    }

    public static String processData(String data) {
        try {
            Thread.sleep(1000); // Simulate processing
            return "Processed: " + data;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error processing data";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== CompletableFuture Demos ===");

        // 1. Basic CompletableFuture
        System.out.println("\n1. Basic CompletableFuture:");
        CompletableFuture<String> basicFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Basic task running on: " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Basic CompletableFuture Result";
        });

        try {
            System.out.println("Result: " + basicFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 2. Chaining with thenApply and thenCompose
        System.out.println("\n2. Chaining Operations:");
        CompletableFuture<String> chainedFuture = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("Step 1 running on: " + Thread.currentThread().getName());
                return "Initial Data";
            })
            .thenApply(data -> {
                System.out.println("Step 2 running on: " + Thread.currentThread().getName());
                return data + " -> Transformed";
            })
            .thenCompose(data -> CompletableFuture.supplyAsync(() -> {
                System.out.println("Step 3 running on: " + Thread.currentThread().getName());
                return data + " -> Final Result";
            }));

        try {
            System.out.println("Chained Result: " + chainedFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 3. Combining multiple CompletableFutures
        System.out.println("\n3. Combining Multiple Futures:");
        String userId = "user123";

        CompletableFuture<String> userDataFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching user data on: " + Thread.currentThread().getName());
            return fetchUserData(userId);
        });

        CompletableFuture<String> preferencesFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching preferences on: " + Thread.currentThread().getName());
            return fetchUserPreferences(userId);
        });

        CompletableFuture<String> combinedFuture = userDataFuture.thenCombine(
            preferencesFuture,
            (userData, preferences) -> {
                System.out.println("Combining data on: " + Thread.currentThread().getName());
                return userData + " + " + preferences;
            }
        );

        try {
            System.out.println("Combined Result: " + combinedFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 4. Exception Handling
        System.out.println("\n4. Exception Handling:");
        CompletableFuture<String> exceptionFuture = CompletableFuture
            .supplyAsync(() -> {
                if (Math.random() > 0.5) {
                    throw new RuntimeException("Simulated error");
                }
                return "Success Result";
            })
            .exceptionally(throwable -> {
                System.out.println("Exception handled: " + throwable.getMessage());
                return "Default Result";
            });

        try {
            System.out.println("Exception handling result: " + exceptionFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 5. Processing multiple items asynchronously
        System.out.println("\n5. Processing Multiple Items:");
        List<String> userIds = Arrays.asList("user1", "user2", "user3", "user4");

        List<CompletableFuture<String>> futures = userIds.stream()
            .map(id -> CompletableFuture.supplyAsync(() -> {
                System.out.println("Processing " + id + " on: " + Thread.currentThread().getName());
                return processData("Data for " + id);
            }))
            .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        CompletableFuture<List<String>> allResults = allFutures.thenApply(v ->
            futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );

        try {
            List<String> results = allResults.get();
            System.out.println("All Results:");
            results.forEach(System.out::println);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 6. Timeout handling
        System.out.println("\n6. Timeout Handling:");
        CompletableFuture<String> timeoutFuture = CompletableFuture
            .supplyAsync(() -> {
                try {
                    Thread.sleep(5000); // Long operation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Long operation result";
            })
            .orTimeout(3, TimeUnit.SECONDS)
            .exceptionally(throwable -> {
                System.out.println("Operation timed out: " + throwable.getMessage());
                return "Timeout fallback result";
            });

        try {
            System.out.println("Timeout result: " + timeoutFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nCompletableFuture demos completed!");
    }
}
