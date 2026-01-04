public class LegacyCOBOLStyleApp {

/* =============================
   IDENTIFICATION DIVISION
   Program: LegacyCOBOLStyleApp
   Author: Your Name
   Date: 2026-01-02
   Description: Example of a Java program
                mimicking COBOL divisions
   ============================= */

/* =============================
   ENVIRONMENT DIVISION
   Using standard input/output
   and thread pool executor
   ============================= */
private static ExecutorService executorService;

/* =============================
   DATA DIVISION
   Variables and data structures
   ============================= */
private static Scanner scanner;
private static List<Future<String>> taskResults;

/* User data POJO */
static class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
}

/* Task for fetching service data */
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

/* =============================
   PROCEDURE DIVISION
   Main program logic
   ============================= */
public static void main(String[] args) {

    // ENVIRONMENT DIVISION setup
    executorService = Executors.newFixedThreadPool(2);
    scanner = new Scanner(System.in);
    taskResults = new ArrayList<>();

    try {
        // DATA DIVISION usage
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        User user = new User(userName, 25); // Age hardcoded for example

        System.out.println("Welcome, " + user.getName() + "!");

        // Submit tasks (simulating services)
        taskResults.add(executorService.submit(new DataFetcher("Service A")));
        taskResults.add(executorService.submit(new DataFetcher("Service B")));

        // Collect task results
        for (Future<String> future : taskResults) {
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
        // ENVIRONMENT cleanup
        executorService.shutdown();
        scanner.close();
    }
}
}
