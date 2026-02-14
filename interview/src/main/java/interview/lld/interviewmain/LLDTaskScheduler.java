package interview.lld.interviewmain;

/*

A Task Scheduler is a system that manages the execution of tasks at predefined times or intervals. It is commonly used in operating systems,
 distributed systems, and backend services to automate jobs like backups, notifications, report generation, and periodic cleanup tasks.

*** The scheduler must ensure these tasks run reliably and at the correct times, even under heavy load or failures.

	•	ScheduledExecutorService
	•	Strategy pattern
	•	Observer pattern
	•	Recurring + one-time tasks
	•	Clean shutdown
 */



import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

enum TaskStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

interface Task {
    String getName();
    void execute() throws Exception;
}

interface SchedulingStrategy {
    Optional<LocalDateTime> nextExecutionTime(LocalDateTime lastExecutionTime);
}

interface TaskExecutionObserver {
    void onTaskStarted(ScheduledTask task);
    void onTaskCompleted(ScheduledTask task);
    void onTaskFailed(ScheduledTask task, Exception exception);
}


class ScheduledTask {

    private final String id;
    private final Task task;
    private final SchedulingStrategy strategy;

    private volatile TaskStatus status = TaskStatus.SCHEDULED;
    private volatile LocalDateTime lastExecutionTime;

    public ScheduledTask(Task task, SchedulingStrategy strategy) {
        this.id = UUID.randomUUID().toString();
        this.task = task;
        this.strategy = strategy;
    }

    public String getId() { return id; }
    public Task getTask() { return task; }

    public Optional<LocalDateTime> nextExecution() {
        return strategy.nextExecutionTime(lastExecutionTime);
    }

    public void markCompleted() {
        lastExecutionTime = LocalDateTime.now();
        status = TaskStatus.COMPLETED;
    }

    public void markRunning() {
        status = TaskStatus.RUNNING;
    }
    public void markFailed() {
        status = TaskStatus.FAILED;
    }
}




class OneTimeStrategy implements SchedulingStrategy {

    private final LocalDateTime executionTime;

    public OneTimeStrategy(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public Optional<LocalDateTime> nextExecutionTime(LocalDateTime lastExecutionTime) {
        return lastExecutionTime == null
                ? Optional.of(executionTime)
                : Optional.empty();
    }
}

class RecurringStrategy implements SchedulingStrategy {

    private final Duration interval;

    public RecurringStrategy(Duration interval) {
        if (interval.isZero() || interval.isNegative())
            throw new IllegalArgumentException("Interval must be positive");
        this.interval = interval;
    }

    @Override
    public Optional<LocalDateTime> nextExecutionTime(LocalDateTime lastExecutionTime) {
        return Optional.of(
                (lastExecutionTime == null
                        ? LocalDateTime.now()
                        : lastExecutionTime).plus(interval)
        );
    }
}


class LoggingObserver implements TaskExecutionObserver {

    @Override
    public void onTaskStarted(ScheduledTask task) {
        System.out.printf("[%s] Task '%s' started%n",
                Thread.currentThread().getName(),
                task.getTask().getName());
    }

    @Override
    public void onTaskCompleted(ScheduledTask task) {
        System.out.printf("[%s] Task '%s' completed%n",
                Thread.currentThread().getName(),
                task.getTask().getName());
    }

    @Override
    public void onTaskFailed(ScheduledTask task, Exception exception) {
        System.err.printf("[%s] Task '%s' failed: %s%n",
                Thread.currentThread().getName(),
                task.getTask().getName(),
                exception.getMessage());
    }
}




class TaskSchedulerService {

    private final ScheduledExecutorService scheduler;
    private final List<TaskExecutionObserver> observers = new CopyOnWriteArrayList<>();

    public TaskSchedulerService(int threadPoolSize) {
        this.scheduler = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public String schedule(Task task, SchedulingStrategy strategy) {
        ScheduledTask scheduledTask = new ScheduledTask(task, strategy);
        scheduleInternal(scheduledTask);
        return scheduledTask.getId();
    }

    private void scheduleInternal(ScheduledTask scheduledTask) {

        Optional<LocalDateTime> nextTimeOpt = scheduledTask.nextExecution();
        if (nextTimeOpt.isEmpty()) return;

        long delay = Duration.between(
                LocalDateTime.now(),
                nextTimeOpt.get()
        ).toMillis();

        scheduler.schedule(() -> executeTask(scheduledTask),
                Math.max(delay, 0),
                TimeUnit.MILLISECONDS);
    }

    private void executeTask(ScheduledTask scheduledTask) {

        scheduledTask.markRunning();
        observers.forEach(o -> o.onTaskStarted(scheduledTask));

        try {
            scheduledTask.getTask().execute();
            scheduledTask.markCompleted();
            observers.forEach(o -> o.onTaskCompleted(scheduledTask));
        } catch (Exception e) {
            scheduledTask.markFailed();
            observers.forEach(o -> o.onTaskFailed(scheduledTask, e));
        }

        // Reschedule if needed
        scheduleInternal(scheduledTask);
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public void addObserver(TaskExecutionObserver observer) {
        observers.add(observer);
    }
}



class PrintMessageTask implements Task {

    private final String message;

    public PrintMessageTask(String message) {
        this.message = message;
    }

    @Override
    public String getName() {
        return message;
    }

    @Override
    public void execute() {
        System.out.printf("[%s] %s%n",
                LocalTime.now().withNano(0),
                message);
    }
}


class DataBackupTask implements Task {

    private final String source;
    private final String destination;

    public DataBackupTask(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String getName() {
        return "DataBackupTask";
    }

    @Override
    public void execute() throws Exception {

        System.out.printf("[%s] Starting backup: %s -> %s%n",
                LocalTime.now().withNano(0),
                source,
                destination);

        // Simulate long-running task
        Thread.sleep(2000);

        System.out.printf("[%s] Backup completed: %s -> %s%n",
                LocalTime.now().withNano(0),
                source,
                destination);
    }
}



public class LLDTaskScheduler {
    static void main() throws InterruptedException {
        // 1. Create Scheduler with 4 worker threads
        TaskSchedulerService scheduler = new TaskSchedulerService(4);

        // 2. Add Observer
        scheduler.addObserver(new LoggingObserver());

        System.out.println("Scheduling tasks...");

        // 3. One-time Task (runs after 2 seconds)
        Task oneTimeTask = new PrintMessageTask("One-time task executed!");
        SchedulingStrategy oneTimeStrategy =
                new OneTimeStrategy(LocalDateTime.now().plusSeconds(2));

        scheduler.schedule(oneTimeTask, oneTimeStrategy);


        // 4. Recurring Task (runs every 3 seconds)
        Task recurringTask = new PrintMessageTask("Recurring task executed!");
        SchedulingStrategy recurringStrategy =
                new RecurringStrategy(Duration.ofSeconds(3));

        scheduler.schedule(recurringTask, recurringStrategy);


        // 5. Long-running task
        Task backupTask =
                new DataBackupTask("/data/source", "/data/backup");

        SchedulingStrategy backupStrategy =
                new OneTimeStrategy(LocalDateTime.now().plusSeconds(4));

        scheduler.schedule(backupTask, backupStrategy);

        System.out.println("Scheduler running... (demo for 12 seconds)");

        // Let it run
        Thread.sleep(12000);

        // 6. Shutdown
        System.out.println("Shutting down scheduler...");
        scheduler.shutdown();
    }
}


/*

Output:-
Scheduling tasks...
Scheduler running... (demo for 12 seconds)
[pool-1-thread-1] Task 'One-time task executed!' started
[00:08:18] One-time task executed!
[pool-1-thread-1] Task 'One-time task executed!' completed
[pool-1-thread-2] Task 'Recurring task executed!' started
[00:08:19] Recurring task executed!
[pool-1-thread-2] Task 'Recurring task executed!' completed
[pool-1-thread-3] Task 'DataBackupTask' started
[00:08:20] Starting backup: /data/source -> /data/backup
[pool-1-thread-1] Task 'Recurring task executed!' started
[00:08:22] Recurring task executed!
[pool-1-thread-1] Task 'Recurring task executed!' completed
[00:08:22] Backup completed: /data/source -> /data/backup
[pool-1-thread-3] Task 'DataBackupTask' completed
[pool-1-thread-1] Task 'Recurring task executed!' started
[00:08:25] Recurring task executed!
[pool-1-thread-1] Task 'Recurring task executed!' completed
Shutting down scheduler...
[pool-1-thread-1] Task 'Recurring task executed!' started
[00:08:28] Recurring task executed!
[pool-1-thread-1] Task 'Recurring task executed!' completed


 */