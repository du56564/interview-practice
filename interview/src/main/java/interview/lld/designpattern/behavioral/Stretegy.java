package interview.lld.designpattern.behavioral;


// Strategy replaces conditional logic with polymorphism.
// Use it when we have different ways of doing the same thing and we want to swap them at runtime.
// ** understand polymorphism and composition over inheritance.

interface PaymentStrategy {
    boolean pay (double amount);
}

class CreditCardPayment implements  PaymentStrategy {

    private String cardNumber;
    public CreditCardPayment (String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " with credit card");
        return true;
    }
}

class PayPalPayment implements PaymentStrategy {

    private String email;
    public PayPalPayment (String email) {
        this.email = email;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " with pay pal");
        return true;
    }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}


public class Stretegy {
    static void main() {
        ShoppingCart shoppingCart = new ShoppingCart();
        PaymentStrategy paymentStrategy = new CreditCardPayment("1234-5674-8932-2344");
        shoppingCart.setPaymentStrategy(paymentStrategy);
        shoppingCart.checkout(1000);

        shoppingCart.setPaymentStrategy(new PayPalPayment("user@example.com"));
        shoppingCart.checkout(50.00);
    }
}
