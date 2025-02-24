package org.example.aggregator;

public interface IPriceUpdate {

    //TODO define and implement

    // currency pair
    // source
    // bid prices and sizes
    // offer prices and sizes

    String getCurrencyPair();
    String getSource();
    Side getSide();
    double getPrice();
    double getVolume();
}
