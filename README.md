# Java Multithreading Demo Suite

A comprehensive collection of Java multithreading demonstrations covering various thread creation methods, communication patterns, and classic synchronization problems.

## Project Structure

```
src/main/java/com/multithreading/
├── MultithreadingDemoRunner.java          # Main runner with interactive menu
├── creation/                              # Thread creation methods
│   ├── ThreadByExtending.java            # Extending Thread class
│   ├── ThreadByRunnable.java             # Implementing Runnable interface
│   ├── ThreadByExecutorFramework.java    # Executor framework demos
│   ├── ThreadByCompletableFuture.java    # CompletableFuture demos
│   └── AdditionalThreadCreationMethods.java # Anonymous, Lambda, Custom pools
├── communication/                         # Thread communication patterns
│   ├── WaitNotifyDemo.java               # Basic wait/notify
│   └── AdvancedThreadCommunication.java  # CountDownLatch, CyclicBarrier, etc.
└── problems/                             # Classic multithreading problems
    ├── ProducerConsumerProblem.java      # Producer-Consumer with multiple implementations
    ├── DeadlockProblem.java              # Deadlock demonstration and prevention
    ├── ReaderWriterProblem.java          # Reader-Writer problem solutions
    ├── DiningPhilosophersProblem.java    # Dining Philosophers solutions
    └── SleepingBarberProblem.java        # Sleeping Barber problem solutions
```

## Thread Creation Methods Covered

### 1. Basic Thread Creation
- **Extending Thread Class**: Direct inheritance approach
- **Implementing Runnable Interface**: Preferred approach for better design

### 2. Advanced Thread Management
- **Executor Framework**: 
  - Fixed Thread Pool
  - Cached Thread Pool
  - Single Thread Executor
  - Scheduled Thread Pool
  - Custom ThreadPoolExecutor
- **CompletableFuture**: Modern asynchronous programming
- **ForkJoinPool**: For parallel processing and divide-and-conquer algorithms

### 3. Additional Methods
- Anonymous Runnable classes
- Lambda expressions
- Custom thread factories
- Virtual Threads (Java 19+ feature - commented for compatibility)

## Thread Communication Patterns

### Basic Communication
- **wait()/notify()**: Fundamental thread coordination
- **synchronized blocks**: Mutual exclusion

### Advanced Communication
- **CountDownLatch**: One-time coordination barrier
- **CyclicBarrier**: Reusable synchronization point
- **Exchanger**: Bidirectional data exchange
- **Phaser**: Multi-phase coordination
- **CompletionService**: Collecting results as they complete
- **BlockingQueue**: Thread-safe producer-consumer

## Classic Multithreading Problems

### 1. Producer-Consumer Problem
**Implementations:**
- Synchronized methods with wait/notify
- ReentrantLock with Conditions
- BlockingQueue (simplest approach)

**Features:**
- Bounded buffer simulation
- Multiple producers and consumers
- Different synchronization strategies

### 2. Deadlock Problem
**Demonstrations:**
- Deadlock creation scenario
- Prevention using ordered locking
- Prevention using timeouts
- Detection and recovery strategies

### 3. Reader-Writer Problem
**Solutions:**
- ReadWriteLock (recommended)
- Synchronized methods with counters
- Semaphore-based implementation
- Priority handling for readers vs writers

### 4. Dining Philosophers Problem
**Approaches:**
- Semaphore-based solution (preventing deadlock)
- Lock-based with asymmetric ordering
- Monitor-based solution using synchronized

### 5. Sleeping Barber Problem
**Implementations:**
- Semaphore coordination
- BlockingQueue approach
- Monitor-based solution

## How to Run

### Prerequisites
- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

### Running the Interactive Demo
1. Compile and run `MultithreadingDemoRunner.java`
2. Use the interactive menu to explore different concepts:
   ```
   =================== MENU ===================
   1. Thread Creation Methods
   2. Thread Communication Patterns
   3. Classic Multithreading Problems
   4. Run All Demos
   0. Exit
   ```

### Running Individual Demos
Each demo class has its own main method and can be run independently:

```bash
# Thread Creation Examples
java com.multithreading.creation.ThreadByExtending
java com.multithreading.creation.ThreadByRunnable
java com.multithreading.creation.ThreadByExecutorFramework
java com.multithreading.creation.ThreadByCompletableFuture

# Communication Examples
java com.multithreading.communication.WaitNotifyDemo
java com.multithreading.communication.AdvancedThreadCommunication

# Classic Problems
java com.multithreading.problems.ProducerConsumerProblem
java com.multithreading.problems.DeadlockProblem
java com.multithreading.problems.ReaderWriterProblem
java com.multithreading.problems.DiningPhilosophersProblem
java com.multithreading.problems.SleepingBarberProblem
```

## Key Learning Outcomes

### Thread Creation
- Understanding different approaches to create threads
- When to use Thread vs Runnable
- Benefits of Executor framework over manual thread management
- Modern asynchronous programming with CompletableFuture

### Synchronization
- Critical sections and race conditions
- Different locking mechanisms
- Performance implications of various approaches
- Deadlock prevention strategies

### Communication Patterns
- Coordination between multiple threads
- Sharing data safely between threads
- Event-driven programming patterns
- Collecting results from concurrent operations

### Problem-Solving Skills
- Analyzing classic concurrency problems
- Implementing multiple solutions for the same problem
- Understanding trade-offs between different approaches
- Debugging multithreaded applications

## Best Practices Demonstrated

1. **Prefer Executor framework** over manual thread creation
2. **Use appropriate synchronization primitives** for the use case
3. **Avoid deadlocks** through proper resource ordering
4. **Use timeout mechanisms** for robust applications
5. **Leverage built-in concurrent collections** when possible
6. **Handle InterruptedException** properly
7. **Use daemon threads** appropriately
8. **Follow naming conventions** for threads

## Common Pitfalls Addressed

- Race conditions and how to prevent them
- Deadlock scenarios and prevention
- Memory consistency issues
- Thread leakage in long-running applications
- Improper exception handling in threads
- Performance issues with excessive synchronization

## Notes

- Some demos may take several minutes to complete as they simulate real-world scenarios
- The deadlock demo intentionally creates deadlock situations for educational purposes
- Thread timing and execution order may vary between runs due to the nature of multithreading
- For production use, always prefer higher-level concurrency utilities over low-level synchronization

## Java Version Compatibility

- **Java 8+**: All features work
- **Java 11+**: Enhanced performance and additional features
- **Java 17+**: Better virtual thread support preparation
- **Java 19+**: Virtual threads (uncomment relevant sections)

This demo suite provides a comprehensive foundation for understanding Java multithreading concepts and can serve as a reference for implementing concurrent solutions in real-world applications.
