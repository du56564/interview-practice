package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

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
