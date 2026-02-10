package interview.lld.paymentgateway.factory;

import interview.lld.paymentgateway.enums.PaymentMethod;
import interview.lld.paymentgateway.strategy.CreditCardProcessor;
import interview.lld.paymentgateway.strategy.PaymentProcessor;
import interview.lld.paymentgateway.strategy.PaypalProcessor;
import interview.lld.paymentgateway.strategy.UPIProcessor;

public class PaymentProcessorFactory {
    public static PaymentProcessor getProcessor(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD -> new CreditCardProcessor();
            case PAYPAL -> new PaypalProcessor();
            case UPI -> new UPIProcessor();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}
