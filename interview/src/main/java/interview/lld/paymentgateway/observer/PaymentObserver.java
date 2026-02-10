package interview.lld.paymentgateway.observer;

import interview.lld.paymentgateway.models.Transaction;

public interface PaymentObserver {
    void onTransactionUpdate(Transaction transaction);
}
