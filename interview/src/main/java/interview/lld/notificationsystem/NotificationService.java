package interview.lld.notificationsystem;


import interview.lld.notificationsystem.decorator.RetryableGatewayDecorator;
import interview.lld.notificationsystem.entities.Notification;
import interview.lld.notificationsystem.factory.NotificationFactory;
import interview.lld.notificationsystem.strategy.NotificationGateway;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Facade and Executor-backed asynchronous orchestrator of the system.
public class NotificationService {
    private final ExecutorService executor;

    public NotificationService(int poolSize) {
        //Uses a thread pool (ExecutorService) for parallel delivery
        this.executor = Executors.newFixedThreadPool(poolSize); // -> return ExecutorService
    }

    public void sendNotification (Notification notification) {
        // Parallel execution of submitted task.
        executor.submit(() -> {
            //Wraps each send operation with retry capability
            NotificationGateway gateway = new RetryableGatewayDecorator(NotificationFactory.createGateway(notification.getType()), 3, 1000);
            try {
                gateway.send(notification);
            } catch (Exception e) {
                System.out.println("Exception while sending notification: " + e);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }



}
