package org.example.aggregator.impl;

import org.example.aggregator.IOrderBook;
import org.example.aggregator.Order;
import org.example.aggregator.Side;

import java.util.ArrayList;
import java.util.List;

public class OrderBook implements IOrderBook {
    private final String currencyPair;
    private final List<Order> bids = new ArrayList<>();
    private final List<Order> asks = new ArrayList<>();

    public OrderBook(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    @Override
    public String getCurrencyPair() {
        return currencyPair;
    }

    @Override
    public List<Order> getBids() {
        return bids;
    }

    @Override
    public List<Order> getAsks() {
        return asks;
    }

    /**
     * Aggregates orders at the same price on the given side.
     */
    public void updateOrder(Side side, double price, double volume) {
        if (side == Side.BID) {
            Order existing = bids.stream().filter(o -> o.getPrice() == price).findFirst().orElse(null);
            if (existing != null) {
                double newVolume = existing.getVolume() + volume;
                bids.remove(existing);
                bids.add(new Order(price, newVolume));
            } else {
                bids.add(new Order(price, volume));
            }
        } else {
            Order existing = asks.stream().filter(o -> o.getPrice() == price).findFirst().orElse(null);
            if (existing != null) {
                double newVolume = existing.getVolume() + volume;
                asks.remove(existing);
                asks.add(new Order(price, newVolume));
            } else {
                asks.add(new Order(price, volume));
            }
        }
    }
}
