package interview.lld.paymentgateway.observer;

import interview.lld.paymentgateway.models.Transaction;

public class MarchantNotifier implements PaymentObserver {
    @Override
    public void onTransactionUpdate(Transaction transaction) {
        System.out.println("--- MERCHANT NOTIFICATION ---");
        System.out.println("Transaction " + transaction.getId() + " status updated to: " + transaction.getStatus());
        System.out.println("-----------------------------");
    }
}
