package interview.lld.paymentgateway.models;

import interview.lld.paymentgateway.enums.PaymentStatus;

public class PaymentResponse {
    private final PaymentStatus status;
    private final String message;

    public PaymentResponse(PaymentStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
