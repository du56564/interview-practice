package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

public class CreditCardProcessor extends AbstractPaymentProcessor{

    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Processing credit card payment of amount " + request.getAmount() + " " + request.getCurrency());
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Credit Card Payment successful.");
    }
}
