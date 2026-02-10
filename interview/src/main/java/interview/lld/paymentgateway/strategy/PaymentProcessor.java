package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

public interface PaymentProcessor {
    PaymentResponse processPayment(PaymentRequest request);
}
