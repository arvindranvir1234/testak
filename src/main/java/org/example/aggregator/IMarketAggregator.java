package org.example.aggregator;

public interface IMarketAggregator {

    /**
     * Where incoming price updates will be passed and processed
     *
     * Assume incoming order book is sweepable
     *
     * @param {@link IPriceUpdate)
     */
    void processPriceUpdate(IPriceUpdate priceUpdate);

    /**
     * When called, should return latest order book for a currency pair which contains aggregated prices
     * from all sources and for both bid and offer
     *
     * NB Ensure that the prices are never crossed
     *
     * @param ccyPair e.g. "EUR/USD"
     * @return {@link IOrderBook} needs to be specified and implemented, must never return null
     */
    IOrderBook getLatestAggregatedOrderBookForCcyPair(String ccyPair);

    /**
     * When called, should return the latest price for that size, calculated for the passed
     * variables and based on the latest aggregated market data
     * for the currency pair
     * @param ccyPair e.g. "EUR/USD"
     * @param volume e.g. 1_500_000d
     * @param side e.g. Side.BID
     * @return should return Double.NaN if not enough liquidity in the market
     */
    double getLatestPrice(String ccyPair, double volume, Side side);


}
