package interview.lld.paymentgateway.strategy;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;

public class UPIProcessor extends AbstractPaymentProcessor {

    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Processing UPI payment of " + request.getAmount() + " " + request.getCurrency());
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "UPI payment successful.");
    }
}
