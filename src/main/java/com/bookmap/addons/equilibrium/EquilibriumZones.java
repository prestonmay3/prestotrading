package com.bookmap.addons.equilibrium;

/**
 * Data class representing the equilibrium zones.
 */
public class EquilibriumZones {

    /**
     * The equilibrium price (midpoint between high and low)
     */
    public final double equilibrium;

    /**
     * The premium zone high (typically session/period high)
     */
    public final double premiumHigh;

    /**
     * The discount zone low (typically session/period low)
     */
    public final double discountLow;

    /**
     * Range of the entire zone
     */
    public final double range;

    public EquilibriumZones(double equilibrium, double premiumHigh, double discountLow) {
        this.equilibrium = equilibrium;
        this.premiumHigh = premiumHigh;
        this.discountLow = discountLow;
        this.range = premiumHigh - discountLow;
    }

    /**
     * Check if a price is in the premium zone
     */
    public boolean isInPremium(double price) {
        return price > equilibrium;
    }

    /**
     * Check if a price is in the discount zone
     */
    public boolean isInDiscount(double price) {
        return price < equilibrium;
    }

    /**
     * Check if a price is at equilibrium (within a small tolerance)
     */
    public boolean isAtEquilibrium(double price, double tolerance) {
        return Math.abs(price - equilibrium) <= tolerance;
    }

    /**
     * Get the percentage of premium zone a price represents
     * 0% = at equilibrium, 100% = at premium high
     */
    public double getPremiumPercentage(double price) {
        if (price <= equilibrium) return 0.0;
        if (price >= premiumHigh) return 100.0;

        double premiumRange = premiumHigh - equilibrium;
        return ((price - equilibrium) / premiumRange) * 100.0;
    }

    /**
     * Get the percentage of discount zone a price represents
     * 0% = at equilibrium, 100% = at discount low
     */
    public double getDiscountPercentage(double price) {
        if (price >= equilibrium) return 0.0;
        if (price <= discountLow) return 100.0;

        double discountRange = equilibrium - discountLow;
        return ((equilibrium - price) / discountRange) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("EquilibriumZones[Premium: %.2f, Equilibrium: %.2f, Discount: %.2f, Range: %.2f]",
            premiumHigh, equilibrium, discountLow, range);
    }
}
