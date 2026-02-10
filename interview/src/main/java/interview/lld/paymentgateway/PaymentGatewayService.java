package interview.lld.paymentgateway;

import interview.lld.paymentgateway.enums.PaymentStatus;
import interview.lld.paymentgateway.factory.PaymentProcessorFactory;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.models.PaymentResponse;
import interview.lld.paymentgateway.models.Transaction;
import interview.lld.paymentgateway.observer.PaymentObserver;
import interview.lld.paymentgateway.strategy.PaymentProcessor;

import java.util.ArrayList;
import java.util.List;

//Template
public class PaymentGatewayService {
    private static PaymentGatewayService instance;
    List<PaymentObserver> paymentObservers = new ArrayList<>();

    private PaymentGatewayService() {}

    public static synchronized PaymentGatewayService getInstance() {
        if (instance == null) {
            instance = new PaymentGatewayService();
        }
        return  instance;
    }


    public Transaction processPayment (PaymentRequest request) {
        Transaction transaction = new Transaction(request);
        try {
            PaymentProcessor processor = PaymentProcessorFactory.getProcessor(request.getPaymentMethod());
            PaymentResponse response = processor.processPayment(request);
            transaction.setStatus(response.getStatus());
        } catch (Exception e) {
            System.err.println("Payment processing failed: " + e.getMessage());
            transaction.setStatus(PaymentStatus.FAILED);
        }
        notifyObservers(transaction);
        return transaction;
    }

    public void addObserver(PaymentObserver paymentObserver) {
        paymentObservers.add(paymentObserver);
    }

    public void removeObserver(PaymentObserver paymentObserver) {
        paymentObservers.remove(paymentObserver);
    }

    public void notifyObservers(Transaction transaction) {
        paymentObservers.forEach(o -> o.onTransactionUpdate(transaction));
    }
}
