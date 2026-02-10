package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

// Template Method Pattern
//The AbstractPaymentProcessor uses this pattern to define a skeleton algorithm for processing a payment (including retries) while allowing subclasses to override the specific doProcess step.
// This avoids code duplication (retry logic) across different processors.
abstract class AbstractPaymentProcessor implements PaymentProcessor {
    private static final int MAX_RETRIES = 3;
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        int attempts = 0;
        PaymentResponse response;
        do {
            response = doProcess(request);
            attempts++;
        } while (response.getStatus() == PaymentStatus.FAILED && attempts < MAX_RETRIES);
        return response;
    }

    protected abstract PaymentResponse doProcess(PaymentRequest request);
}
