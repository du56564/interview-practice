package interview.lld;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

/*
A Task Scheduler is a system that manages the execution of tasks at predefined times or intervals. It is commonly used in operating systems,
 distributed systems, and backend services to automate jobs like backups, notifications, report generation, and periodic cleanup tasks.

*** The scheduler must ensure these tasks run reliably and at the correct times, even under heavy load or failures.

Requirement:
    - Schedule One-Time Tasks
    - Schedule Recurring Task
    - Execution and Coordination
    - Cancelling scheduled task
    - Notify observer when task start
    - Track task status through lifecycle


Core Entity:
    - TaskSchedulerService (Queue, Worker, Observer)
    - ScheduledTask (Task, Strategy, nextExecTime, status)
    - TaskExecute
    - ScheduleStrategy
    - TaskExecutionObserver (onStart, onComplete, onFailed)
    - TaskStatus (SCHEDULED, RUNNING, COMPLETED, FAILED, CANCELLED)


 */
enum TaskStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED
}

class TaskSchedulerException extends  RuntimeException {
    public TaskSchedulerException(String message) {
        super(message);
    }
    public TaskSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}

//Command pattern
interface Task {
    String getName();
    void execute();
}

//Strategy Pattern
interface SchedulingStrategy {
    Optional<LocalDateTime> getNextExecutionTime(LocalDateTime localExecutionTime);
}

// Observer Pattern
interface TaskExecutionObserver {
    void onTaskStarted(ScheduledTask task);
    void onTaskCompleted(ScheduledTask task);
    void onTaskFailed(ScheduledTask task, Exception exception);
}

class ScheduledTask implements Comparable<ScheduledTask> {
    private final String id;
    private final Task task;
    private final SchedulingStrategy strategy;
    private LocalDateTime nextExecutionTime;
    private LocalDateTime lastExecutionTime;

    public ScheduledTask(Task task, SchedulingStrategy strategy) {
        this.id = UUID.randomUUID().toString();
        this.task = task;
        this.strategy = strategy;
        updateNextExecutionTime();
    }

    public void updateNextExecutionTime() {
        Optional<LocalDateTime> nextTime = strategy.getNextExecutionTime(this.lastExecutionTime);
        this.nextExecutionTime = nextTime.orElse(null);
    }

    public void updateLastExecutionTime() {
        this.lastExecutionTime = nextExecutionTime;
    }

    @Override
    public int compareTo(ScheduledTask other) {
        return this.nextExecutionTime.compareTo(other.nextExecutionTime);
    }

    // Getters
    public String getId() { return id; }
    public Task getTask() { return task; }
    public LocalDateTime getNextExecutionTime() { return nextExecutionTime; }
    public boolean hasMoreExecutions() { return nextExecutionTime != null; }
}

class OneTimeSchedulingStrategy implements SchedulingStrategy {
    private final LocalDateTime executionTime;
    public OneTimeSchedulingStrategy(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
    @Override
    public Optional<LocalDateTime> getNextExecutionTime(LocalDateTime localExecutionTime) {
        if (localExecutionTime == null) {
            return Optional.of(executionTime);
        }
        return Optional.empty();
    }
}

class RecurringSchedulingStrategy implements SchedulingStrategy {
    private final Duration interval;
    public RecurringSchedulingStrategy(Duration interval) {
        // Fail fast: zero or negative intervals make no sense for recurring tasks
        if (interval.isNegative() || interval.isZero()) {
            throw new IllegalArgumentException("Interval must be positive");
        }
        this.interval = interval;
    }
    @Override
    public Optional<LocalDateTime> getNextExecutionTime(LocalDateTime lastExecutionTime) {
        // First execution: schedule relative to now
        // Subsequent executions: schedule relative to when the task last finished
        LocalDateTime baseTime = (lastExecutionTime != null)
                ? lastExecutionTime
                : LocalDateTime.now();
        return Optional.of(baseTime.plus(interval));
    }
}

class PrintMessageTask implements Task {
    private final String message;

    public PrintMessageTask(String message) {
        this.message = message;
    }

    @Override
    public String getName() { return message; }

    @Override
    public void execute() {
        // withNano(0) truncates nanoseconds for cleaner log output
        System.out.printf("[%s] %s%n", LocalTime.now().withNano(0), message);
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
    public String getName() { return "DataBackupTask"; }

    @Override
    public void execute() {
        System.out.printf("[%s] Starting backup: %s -> %s%n",
                LocalTime.now().withNano(0), source, destination);
        try {
            Thread.sleep(2000);  // Simulate I/O-bound work
        } catch (InterruptedException e) {
            // Restore the interrupt flag so the caller (worker thread)
            // knows this thread was interrupted during execution
            Thread.currentThread().interrupt();
        }
        System.out.printf("[%s] Backup completed: %s -> %s%n",
                LocalTime.now().withNano(0), source, destination);
    }
}

class LoggingObserver implements TaskExecutionObserver {
    @Override
    public void onTaskStarted(ScheduledTask task) {
        // Include thread name so logs show which worker executed which task
        System.out.printf("[%s] Task '%s' started%n",
                Thread.currentThread().getName(), task.getTask().getName());
    }

    @Override
    public void onTaskCompleted(ScheduledTask task) {
        System.out.printf("[%s] Task '%s' completed%n",
                Thread.currentThread().getName(), task.getTask().getName());
    }

    @Override
    public void onTaskFailed(ScheduledTask task, Exception exception) {
        // Use stderr for failures so they stand out in logs
        System.err.printf("[%s] Task '%s' failed: %s%n",
                Thread.currentThread().getName(), task.getTask().getName(),
                exception.getMessage());
    }
}


// PriorityQueue + wait/notify + observer + worker
class TaskSchedulerService {
    private static final TaskSchedulerService INSTANCE = new TaskSchedulerService();
    private final PriorityBlockingQueue<ScheduledTask> taskQueue = new PriorityBlockingQueue<>();
    private final List<TaskExecutionObserver> observers = new ArrayList<>();
    private Thread[] workers;
    private volatile boolean running = true;

    private TaskSchedulerService() {}

    public static TaskSchedulerService getInstance() {
        return INSTANCE;
    }

    public void initialize(int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("Worker count must be >= 1");
        }
        workers = new Thread[workerCount];
        startWorkers();
    }

    public String schedule(Task task, SchedulingStrategy strategy) {
        ScheduledTask scheduledTask = new ScheduledTask(task, strategy);
        taskQueue.put(scheduledTask);
        return scheduledTask.getId();
    }

    private void startWorkers() {
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(this::runWorker, "WorkerThread-" + i);
            workers[i].setDaemon(true);
            workers[i].start();
        }
    }

    private void runWorker() {
        while (running) {
            try {
                // take() blocks until an element is available.
                ScheduledTask task = taskQueue.take();
                LocalDateTime now = LocalDateTime.now();
                long waitTime = 0;

                if (task.getNextExecutionTime().isAfter(now)) {
                    waitTime = Duration.between(now, task.getNextExecutionTime()).toMillis();
                }

                if (waitTime > 0) {
                    // Wait for the scheduled time.
                    Thread.sleep(waitTime);
                }

                // Check if a higher-priority task has arrived while we were sleeping
                ScheduledTask head = taskQueue.peek();
                if (head != null && head.compareTo(task) < 0) {
                    taskQueue.put(task); // Put our task back and let the higher-priority one run
                    continue;
                }

                // --- Execute the task ---
                execute(task);
            } catch (InterruptedException e) {
                // This is the expected way to stop the worker thread.
                Thread.currentThread().interrupt();
                break; // Exit the loop
            }
        }
        System.out.printf("%s stopped.%n", Thread.currentThread().getName());
    }

    private void execute(ScheduledTask task) {
        observers.forEach(o -> o.onTaskStarted(task));
        try {
            task.getTask().execute();
            task.updateLastExecutionTime();
            observers.forEach(o -> o.onTaskCompleted(task));
        } catch (Exception e) {
            observers.forEach(o -> o.onTaskFailed(task, e));
            System.err.printf("Task %s failed with error: %s%n", task.getId(), e.getMessage());
        } finally {
            // --- Re-scheduling logic ---
            // Must be done whether the task succeeded or failed.
            task.updateNextExecutionTime();

            if (task.hasMoreExecutions()) {
                taskQueue.put(task); // Re-queue for the next run.
            } else {
                System.out.printf("Task %s has no more executions and will not be rescheduled.%n", task.getId());
            }
        }
    }

    public void shutdown() {
        running = false;
        for (Thread worker : workers) {
            worker.interrupt(); // in case they're blocked on take()
        }
        System.out.println("Scheduler shut down.");
    }

    public void addObserver(TaskExecutionObserver observer) {
        observers.add(observer);
    }
}



public class LLDTaskScheduler {
    static void main() throws InterruptedException {
        // 1. Setup the facade and observers
        TaskSchedulerService scheduler = TaskSchedulerService.getInstance();
        scheduler.addObserver(new LoggingObserver());

        // 2. Initialize the scheduler
        scheduler.initialize(10);

        // 3. Define tasks and strategies
        // Scenario 1: One-time task, 5 seconds from now
        Task oneTimeTask = new PrintMessageTask("This is a one-time task.");
        SchedulingStrategy oneTimeStrategy = new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(1));

        // Scenario 2: Recurring task, every 3 seconds
        Task recurringTask = new PrintMessageTask("This is a recurring task.");
        SchedulingStrategy recurringStrategy = new RecurringSchedulingStrategy(Duration.ofSeconds(2));

        // Scenario 3: A long-running backup task, scheduled to run in 1 second
        Task backupTask = new DataBackupTask("/data/source", "/data/backup");
        SchedulingStrategy longRunningRecurringStrategy = new OneTimeSchedulingStrategy(LocalDateTime.now().plusSeconds(3));

        // 4. Schedule the tasks using the facade
        System.out.println("Scheduling tasks...");
        scheduler.schedule(oneTimeTask, oneTimeStrategy);
        scheduler.schedule(recurringTask, recurringStrategy);
        scheduler.schedule(backupTask, longRunningRecurringStrategy);

        // 5. Let the demo run for a while
        System.out.println("Scheduler is running. Waiting for tasks to execute... (Demo will run for 10 seconds)");
        Thread.sleep(6000);

        // 6. Shutdown the scheduler
        scheduler.shutdown();
    }

}
