package interview.lld.notificationsystem.decorator;

import interview.lld.notificationsystem.entities.Notification;
import interview.lld.notificationsystem.strategy.NotificationGateway;
/*
Automatically retries failed sends up to a defined number of attempts
Adds delay between retries and logs retry attempts
 */
public class RetryableGatewayDecorator implements NotificationGateway{
    private final NotificationGateway wrappedGateway;
    private final int maxRetries;
    private final long retryDelayMillis;

    public RetryableGatewayDecorator(NotificationGateway wrappedGateway, int maxRetries, long retryDelayMillis) {
        this.wrappedGateway = wrappedGateway;
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }


    @Override
    public void send(Notification notification) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                wrappedGateway.send(notification);
                return;
            } catch (Exception e) {
                attempt++;
                System.out.println("Error: Attempt " + attempt + " failed for notification " + notification.getId() + ". Retrying...");
                if (attempt >= maxRetries) {
                    System.out.println(e.getMessage());
                    throw new Exception("Failed to send notification after " + maxRetries + " attempts.", e);
                }
                Thread.sleep(retryDelayMillis);
            }
        }
    }
}
