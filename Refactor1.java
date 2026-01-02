import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

public class ModernApplication {

    public static void main(String[] args) {

        // Structured Concurrency (Java 21)
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var serviceATask = scope.fork(() -> fetchData("Service A"));
            var serviceBTask = scope.fork(() -> fetchData("Service B"));

            scope.join();            // Wait for all tasks
            scope.throwIfFailed();   // Fail fast on error

            System.out.println(serviceATask.get());
            System.out.println(serviceBTask.get());

        } catch (Exception e) {
            System.err.println("Execution failed: " + e.getMessage());
        }
    }

    // Virtual-thread-friendly method
    static String fetchData(String serviceName) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
        return serviceName + " response";
    }

    // Modern data carrier
    record User(String name, int age) {}
}
