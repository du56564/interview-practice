package interview.lld.designpattern.behavioral;


//Observer lets objects subscribe to events and get notified when something happens.
//changes—a stock price changes and multiple displays need to update,
// or a user places an order and inventory, notifications, and analytics
// "notify" or "update multiple components
// When the stock price changes, every attached observer gets updated automatically


import java.util.ArrayList;
import java.util.List;

//like publisher - manage observers
interface Subject {
    void attach(Observer observer);
    void detach (Observer observer);
    void notifyObservers();
}

//like Consumer
interface Observer {
    void update(String symbol, double price);
}
// ***** Template above of Observer/Subject ****** //


class Stock implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String symbol;
    private double price;

    public Stock (String symbol) {
        this.symbol = symbol;
    }

    public void setPrice (double price) {
        this.price = price;
        notifyObservers();
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(symbol, price);
        }
    }
}

class PriceDisplay implements Observer {
    public void update(String symbol, double price) {
        System.out.println("Display updated: " + symbol + " = $" + price);
    }
}

class PriceAlert implements Observer {
    private double threshold;

    public PriceAlert(double threshold) {
        this.threshold = threshold;
    }

    public void update(String symbol, double price) {
        if (price > threshold) {
            System.out.println("Alert! " + symbol + " exceeded $" + threshold);
        }
    }
}


public class ObserverPattern {
    static void main() {
        Stock stock = new Stock("APL");
        PriceDisplay priceDisplay = new PriceDisplay();
        PriceAlert priceAlert = new PriceAlert(150.0);
        stock.attach(priceDisplay);
        stock.attach(priceAlert);
        stock.setPrice(145);
        stock.setPrice(200);
    }
}
