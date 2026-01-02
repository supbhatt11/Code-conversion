import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.StructuredTaskScope;

public class ModernCOBOLStyleApp {

    /* =============================
       IDENTIFICATION DIVISION
       Program: ModernCOBOLStyleApp
       Author: Your Name
       Date: 2026-01-02
       Description: Modernized Java 21 version
                    of a COBOL-like legacy program
       ============================= */

    /* =============================
       ENVIRONMENT DIVISION
       Standard input/output
       Structured concurrency for tasks
       ============================= */
    private static Scanner scanner;

    /* =============================
       DATA DIVISION
       Variables and data structures
       ============================= */
    record User(String name, int age) {}

    /* =============================
       PROCEDURE DIVISION
       Main program logic
       ============================= */
    public static void main(String[] args) {

        // ENVIRONMENT DIVISION setup
        scanner = new Scanner(System.in);

        // DATA DIVISION usage
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        User user = new User(userName, 25); // Example age

        System.out.println("Welcome, " + user.name() + "!");

        // PROCEDURE DIVISION: Execute tasks with virtual threads
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var taskA = scope.fork(() -> fetchServiceData("Service A"));
            var taskB = scope.fork(() -> fetchServiceData("Service B"));

            scope.join();            // Wait for all tasks
            scope.throwIfFailed();   // Fail-fast if any task fails

            System.out.println(taskA.get());
            System.out.println(taskB.get());

        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        } finally {
            scanner.close(); // ENVIRONMENT cleanup
        }
    }

    // Modern virtual-thread-friendly task
    static String fetchServiceData(String serviceName) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1)); // Simulate service call
        return serviceName + " response";
    }
}
