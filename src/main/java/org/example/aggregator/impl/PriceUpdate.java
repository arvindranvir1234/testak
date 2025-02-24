package org.example.aggregator.impl;

import org.example.aggregator.IPriceUpdate;
import org.example.aggregator.Side;

public class PriceUpdate implements IPriceUpdate {
    private final String currencyPair;
    private final Side side;
    private final double price;
    private final double volume;
    private final String source;

    public PriceUpdate(String currencyPair, Side side, double price, double volume, String source) {
        this.currencyPair = currencyPair;
        this.side = side;
        this.price = price;
        this.volume = volume;
        this.source = source;
    }
    @Override
    public String getCurrencyPair() {
        return currencyPair;
    }
    @Override
    public Side getSide() {
        return side;
    }
    @Override
    public double getPrice() {
        return price;
    }
    @Override
    public double getVolume() {
        return volume;
    }
    public String getSource() {
        return source;
    }
}