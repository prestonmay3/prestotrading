package com.bookmap.addons.equilibrium;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Calculates premium, equilibrium, and discount zones based on price action.
 *
 * Equilibrium is calculated as the midpoint between high and low over a period.
 * Premium zone is from equilibrium to high.
 * Discount zone is from equilibrium to low.
 */
public class EquilibriumCalculator {

    private Queue<PricePoint> priceHistory;
    private int lookbackPeriod;
    private boolean useSessionHighLow;
    private double pips;

    private double sessionHigh = Double.NaN;
    private double sessionLow = Double.NaN;
    private long sessionStartTime = 0;

    public EquilibriumCalculator(int lookbackPeriod, boolean useSessionHighLow, double pips) {
        this.lookbackPeriod = lookbackPeriod;
        this.useSessionHighLow = useSessionHighLow;
        this.pips = pips;
        this.priceHistory = new LinkedList<>();
    }

    /**
     * Add a new price point to the calculator
     */
    public void addPrice(double price, long timestamp) {
        // Add to price history
        priceHistory.offer(new PricePoint(price, timestamp));

        // Maintain lookback period size
        while (priceHistory.size() > lookbackPeriod) {
            priceHistory.poll();
        }

        // Update session high/low
        updateSessionHighLow(price, timestamp);
    }

    /**
     * Calculate the equilibrium zones
     */
    public EquilibriumZones calculateZones() {
        if (priceHistory.isEmpty()) {
            return null;
        }

        double high, low;

        if (useSessionHighLow && !Double.isNaN(sessionHigh) && !Double.isNaN(sessionLow)) {
            // Use session high/low
            high = sessionHigh;
            low = sessionLow;
        } else {
            // Use lookback period high/low
            high = Double.NEGATIVE_INFINITY;
            low = Double.POSITIVE_INFINITY;

            for (PricePoint point : priceHistory) {
                if (point.price > high) {
                    high = point.price;
                }
                if (point.price < low) {
                    low = point.price;
                }
            }

            if (high == Double.NEGATIVE_INFINITY || low == Double.POSITIVE_INFINITY) {
                return null;
            }
        }

        // Calculate equilibrium (midpoint)
        double equilibrium = (high + low) / 2.0;

        // Calculate zone boundaries
        // Premium zone: equilibrium to high
        // Discount zone: low to equilibrium

        return new EquilibriumZones(equilibrium, high, low);
    }

    /**
     * Update session high/low (resets every 24 hours)
     */
    private void updateSessionHighLow(double price, long timestamp) {
        // Check if new session (24 hours = 86400000 ms)
        if (sessionStartTime == 0 || timestamp - sessionStartTime > 86400000L) {
            sessionStartTime = timestamp;
            sessionHigh = price;
            sessionLow = price;
        } else {
            if (price > sessionHigh) {
                sessionHigh = price;
            }
            if (price < sessionLow) {
                sessionLow = price;
            }
        }
    }

    // Getters and setters
    public void setLookbackPeriod(int lookbackPeriod) {
        this.lookbackPeriod = lookbackPeriod;

        // Trim history if necessary
        while (priceHistory.size() > lookbackPeriod) {
            priceHistory.poll();
        }
    }

    public void setUseSessionHighLow(boolean useSessionHighLow) {
        this.useSessionHighLow = useSessionHighLow;
    }

    /**
     * Internal class to store price points with timestamps
     */
    private static class PricePoint {
        double price;
        long timestamp;

        PricePoint(double price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }
}
