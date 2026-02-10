package interview.lld.paymentgateway;

/*
A payment gateway is a critical component in any online transaction system. It acts as a bridge between the user, the merchant,
and financial institutions by securely processing payment requests, verifying details, and ensuring funds are transferred correctly.

For example, when a customer purchases a product on an e-commerce platform, the payment gateway handles the steps of capturing payment details,
validating them, interacting with the bank or wallet provider, and communicating the result (success or failure) to the application.

Requirements
    - multiple payment method: Credit Card, PayPal, Phonepe
    - retry = 3
    - notification (merchant, cosumer)

Core Entities
    - PaymentMethod & PaymentStatus
    - PaymentRequest
    - PaymentResponse
    - Transaction
    - PaymentProcessor
    - PaymentProcessorFactory
    - PaymentObserver
    - PaymentGatewayService


Design Pattern
    - Strategy
    - Observer
    - Factory
    - Builder
    - Template
    - Facade & Singleton

 */

import interview.lld.paymentgateway.enums.PaymentMethod;
import interview.lld.paymentgateway.models.PaymentRequest;
import interview.lld.paymentgateway.observer.CustomerNotifier;
import interview.lld.paymentgateway.observer.MarchantNotifier;

import java.util.Map;

public class MainPaymentGateWay {
    static void main() {
        PaymentGatewayService paymentGateway =  PaymentGatewayService.getInstance();
        paymentGateway.addObserver(new MarchantNotifier());
        paymentGateway.addObserver(new CustomerNotifier());

        System.out.println("----------- SCENARIO 1: Successful Credit Card Payment -----------");
        PaymentRequest ccRequest = new PaymentRequest.Builder()
                .payerId("U-123")
                .amount(150.75)
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentDetails(Map.of("cardNumber", "1234..."))
                .build();

        paymentGateway.processPayment(ccRequest);

        System.out.println("\n----------- SCENARIO 2: Successful PayPal Payment -----------");
        PaymentRequest paypalRequest = new PaymentRequest.Builder()
                .payerId("U-456")
                .amount(88.50)
                .currency("USD")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentDetails(Map.of("email", "customer@example.com"))
                .build();

        paymentGateway.processPayment(paypalRequest);
    }

/*

Output:

----------- SCENARIO 1: Successful Credit Card Payment -----------
Processing credit card payment of amount 150.75 INR
--- MERCHANT NOTIFICATION ---
Transaction 040f8d17-656b-4ade-80b1-b3b303224721 status updated to: SUCCESSFUL
-----------------------------
--- CUSTOMER EMAIL ---
Your payment of 150.75 was successful. Transaction ID: 040f8d17-656b-4ade-80b1-b3b303224721
----------------------

----------- SCENARIO 2: Successful PayPal Payment -----------
Redirecting to PayPal for transaction 39b87cee-a24e-44d0-af23-877d92f3006f
--- MERCHANT NOTIFICATION ---
Transaction 39b87cee-a24e-44d0-af23-877d92f3006f status updated to: SUCCESSFUL
-----------------------------
--- CUSTOMER EMAIL ---
Your payment of 88.5 was successful. Transaction ID: 39b87cee-a24e-44d0-af23-877d92f3006f
----------------------


 */

}
