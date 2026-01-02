import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LegacyApplication {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = new ArrayList<Future<String>>();

        try {
            futures.add(executorService.submit(new DataFetcher("Service A")));
            futures.add(executorService.submit(new DataFetcher("Service B")));

            for (Future<String> future : futures) {
                try {
                    String result = future.get(); // blocking call
                    System.out.println(result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread interrupted");
                } catch (ExecutionException e) {
                    System.err.println("Execution failed: " + e.getMessage());
                }
            }

        } finally {
            executorService.shutdown();
        }
    }

    // Legacy-style POJO (no records)
    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    // Legacy Callable implementation
    static class DataFetcher implements Callable<String> {

        private String serviceName;

        public DataFetcher(String serviceName) {
            this.serviceName = serviceName;
        }

        @Override
        public String call() throws Exception {
            Thread.sleep(1000);
            return serviceName + " response";
        }
    }
}
