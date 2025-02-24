package org.example.aggregator;

public class Order {
    private final double price;
    private final double volume;

    public Order(double price, double volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Order{" +
                "price=" + price +
                ", volume=" + volume +
                '}';
    }
}
