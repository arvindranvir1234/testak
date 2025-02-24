package org.example.aggregator.impl;

import org.example.aggregator.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketAggregatorImpl implements IMarketAggregator {

    // Store order books by currency pair.
    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    @Override
    public void processPriceUpdate(IPriceUpdate priceUpdate) {
        String ccyPair = priceUpdate.getCurrencyPair();
        // Get or create the order book for this currency pair.
        OrderBook orderBook = orderBooks.computeIfAbsent(ccyPair, OrderBook::new);

        // Update the order book based on the incoming price update.
        orderBook.updateOrder(priceUpdate.getSide(), priceUpdate.getPrice(), priceUpdate.getVolume());

        // Resolve potential crossed order book scenarios.
        resolveCrossedBook(orderBook);
    }

    @Override
    public IOrderBook getLatestAggregatedOrderBookForCcyPair(String ccyPair) {
        // Always return a valid order book, even if empty.
        return orderBooks.computeIfAbsent(ccyPair, OrderBook::new);
    }

    @Override
    public double getLatestPrice(String ccyPair, double volume, Side side) {
        IOrderBook orderBook = getLatestAggregatedOrderBookForCcyPair(ccyPair);
        double accumulatedVolume = 0.0;
        double lastPrice = Double.NaN;

        // For BID side, best price is assumed to be the highest bid.
        if (side == Side.BID) {
            // Note: In a production system, you would maintain orders sorted by price.
            for (Order order : orderBook.getBids()) {
                accumulatedVolume += order.getVolume();
                lastPrice = order.getPrice();
                if (accumulatedVolume >= volume) {
                    return lastPrice;
                }
            }
        } else { // For ASK side, best price is the lowest ask.
            for (Order order : orderBook.getAsks()) {
                accumulatedVolume += order.getVolume();
                lastPrice = order.getPrice();
                if (accumulatedVolume >= volume) {
                    return lastPrice;
                }
            }
        }
        // Return NaN if there's not enough liquidity.
        return Double.NaN;
    }

    /**
     * Checks if the aggregated order book is crossed, i.e., if the highest bid is equal or higher than the lowest ask.
     * In this simple implementation, if the book is crossed, both sides are cleared.
     */
    private void resolveCrossedBook(OrderBook orderBook) {
        Double bestBid = orderBook.getBids().stream()
                .map(Order::getPrice)
                .max(Double::compareTo)
                .orElse(null);
        Double bestAsk = orderBook.getAsks().stream()
                .map(Order::getPrice)
                .min(Double::compareTo)
                .orElse(null);
        if (bestBid != null && bestAsk != null && bestBid >= bestAsk) {
            // Clear both sides if crossed.
            orderBook.getBids().clear();
            orderBook.getAsks().clear();
        }
    }
}
