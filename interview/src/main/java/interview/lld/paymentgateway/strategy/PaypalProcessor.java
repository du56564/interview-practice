package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

public class PaypalProcessor extends AbstractPaymentProcessor{

    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Redirecting to PayPal for transaction " + request.getTransactionId());
        // Simulate PayPal API interaction
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Paypal payment successful.");
    }
}
