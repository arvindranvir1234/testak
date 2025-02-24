package org.example.aggregator.impl;

import org.example.aggregator.*;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


class MarketAggregatorImplTest {

    /**
     * 1. Test multiple market updates coming from at least three different sources.
     */
    @Test
    public void testMultipleSourcesAggregation() {
        IMarketAggregator aggregator = new MarketAggregatorImpl();
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.10, 1_000_000, "SourceA"));
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.10, 500_000, "SourceB"));
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.OFFER, 1.11, 1_000_000, "SourceC"));


        IOrderBook orderBook = aggregator.getLatestAggregatedOrderBookForCcyPair("EUR/USD");

        // Verify aggregated bid at 1.10 has combined volume.
        Order bidOrder = orderBook.getBids().stream()
                .filter(o -> o.getPrice() == 1.10)
                .findFirst()
                .orElse(null);
        assertNotNull("Bid order at 1.10 should exist",bidOrder);
        assertEquals("Aggregated volume for 1.10 bid should be 1,500,000",1_500_000.00, bidOrder.getVolume());

        // Verify offer order exists.
        Order askOrder = orderBook.getAsks().stream()
                .filter(o -> o.getPrice() == 1.11)
                .findFirst()
                .orElse(null);
        assertNotNull("Ask order at 1.11 should exist",askOrder);
        assertEquals("Volume for 1.11 ask should be 1,000,000",1_000_000.00, askOrder.getVolume());
    }

    /**
     * 2. Test market updates for different currency pairs from the same source.
     */
    @Test
    public void testDifferentCurrencyPairsSameSource() {
        IMarketAggregator aggregator = new MarketAggregatorImpl();
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.10, 1_000_000, "SourceA"));
        aggregator.processPriceUpdate(new PriceUpdate("GBP/USD", Side.OFFER, 1.30, 1_000_000, "SourceA"));


        IOrderBook eurOrderBook = aggregator.getLatestAggregatedOrderBookForCcyPair("EUR/USD");
        IOrderBook gbpOrderBook = aggregator.getLatestAggregatedOrderBookForCcyPair("GBP/USD");

        assertNotNull("EUR/USD order book should not be null",eurOrderBook);
        assertNotNull("GBP/USD order book should not be null",gbpOrderBook);
        assertNotEquals(eurOrderBook, gbpOrderBook, "Order books for different currency pairs should be different");
    }

    /**
     * 3. There's been market updates for same currency pair but different sources,
     * which contain same prices for some levels on the same side
     */
    @Test
    public void testAggregationOfSamePriceLevels() {
        IMarketAggregator aggregator = new MarketAggregatorImpl();
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.10, 1_000_000, "SourceA"));
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.10, 500_000, "SourceB"));

        IOrderBook orderBook = aggregator.getLatestAggregatedOrderBookForCcyPair("EUR/USD");
        long count = orderBook.getBids().stream()
                .filter(o -> o.getPrice() == 1.10)
                .count();
        assertEquals("There should be a single bid order level at 1.10 after aggregation",1, count);

        Order bidOrder = orderBook.getBids().stream()
                .filter(o -> o.getPrice() == 1.10)
                .findFirst()
                .orElse(null);
        assertNotNull("Bid order at 1.10 should exist",bidOrder );
        assertEquals("Aggregated volume for 1.10 bid should be 1,500,000",1_500_000.00, bidOrder.getVolume());
    }

    /**
     * 4. Test a market update that causes the aggregated book to become crossed.
     *    simple resolution strategy clears both sides.
     */
    @Test
    public void testCrossedOrderBookResolution() {
        IMarketAggregator aggregator = new MarketAggregatorImpl();
        // Process an update that will cause crossing: bid at 1.12 and ask at 1.11.
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.BID, 1.12, 1_000_000, "SourceA"));
        aggregator.processPriceUpdate(new PriceUpdate("EUR/USD", Side.OFFER, 1.11, 1_000_000, "SourceB"));

        IOrderBook orderBook = aggregator.getLatestAggregatedOrderBookForCcyPair("EUR/USD");

        // According to simple crossing resolution, both sides should be cleared.
        assertTrue(orderBook.getBids().isEmpty() && orderBook.getAsks().isEmpty(), "Crossed order book should be resolved by clearing orders");
    }
}