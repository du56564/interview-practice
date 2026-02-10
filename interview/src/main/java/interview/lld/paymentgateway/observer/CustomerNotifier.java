package interview.lld.paymentgateway.observer;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.models.Transaction;

public class CustomerNotifier implements PaymentObserver {
    @Override
    public void onTransactionUpdate(Transaction transaction) {
        if (transaction.getStatus() == PaymentStatus.SUCCESSFUL) {
            System.out.println("--- CUSTOMER EMAIL ---");
            System.out.println("Your payment of " + transaction.getRequest().getAmount() + " was successful. Transaction ID: " + transaction.getId());
            System.out.println("----------------------");
        }
    }
}
