package interview.lld.notificationsystem.factory;

import interview.lld.notificationsystem.enums.NotificationType;
import interview.lld.notificationsystem.strategy.EmailGateway;
import interview.lld.notificationsystem.strategy.NotificationGateway;
import interview.lld.notificationsystem.strategy.PushGateway;
import interview.lld.notificationsystem.strategy.SMSGateway;

import java.util.HashMap;
import java.util.Map;

public class NotificationFactory {
    //Uses caching (gatewayMap) to reuse gateway instances
    private static final Map<NotificationType, NotificationGateway> gatewayMap = new HashMap<>();

    public static NotificationGateway createGateway (NotificationType type) {
        if(gatewayMap.containsKey(type)) {
            return gatewayMap.get(type);
        }
        NotificationGateway gateway = null;
        switch (type) {
            case EMAIL -> gateway = new EmailGateway();
            case SMS -> gateway = new SMSGateway();
            case PUSH -> gateway = new PushGateway();
        }
        gatewayMap.put(type, gateway);
        return gateway;
    }
}
