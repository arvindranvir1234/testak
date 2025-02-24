package org.example.aggregator;

import java.util.List;

public interface IOrderBook {


    // ccy pair
    // aggregated bid sizes and prices
    // aggregated offer sizes and prices

    String getCurrencyPair();
    List<Order> getBids();
    List<Order> getAsks();
}
