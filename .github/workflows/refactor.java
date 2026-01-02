import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

public class ModernApplication {

    public static void main(String[] args) {

        // Structured Concurrency (Java 21)
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var taskA = scope.fork(() -> fetchData("Service A"));
            var taskB = scope.fork(() -> fetchData("Service B"));

            scope.join();            // Wait for all tasks
            scope.throwIfFailed();   // Fail fast if any task fails

            System.out.println(taskA.get());
            System.out.println(taskB.get());

        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        }
    }

    // Virtual-thread friendly task
    static String fetchData(String serviceName) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
        return serviceName + " response";
    }

    // Modern replacement for legacy POJO
    record User(String name, int age) {}
}
