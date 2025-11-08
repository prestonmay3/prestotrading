package com.bookmap.addons.equilibrium;

import velox.api.layer1.Layer1ApiAdminAdapter;
import velox.api.layer1.Layer1ApiFinishable;
import velox.api.layer1.Layer1ApiInstrumentAdapter;
import velox.api.layer1.Layer1ApiProvider;
import velox.api.layer1.annotations.Layer1ApiVersion;
import velox.api.layer1.annotations.Layer1ApiVersionValue;
import velox.api.layer1.annotations.Layer1Attachable;
import velox.api.layer1.annotations.Layer1StrategyName;
import velox.api.layer1.common.Log;
import velox.api.layer1.data.InstrumentInfo;
import velox.api.layer1.data.TradeInfo;
import velox.api.layer1.layers.utils.OrderBook;
import velox.api.layer1.messages.indicators.*;
import velox.gui.StrategyPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Bookmap addon that displays Premium, Equilibrium, and Discount zones
 * based on price action analysis.
 */
@Layer1Attachable
@Layer1StrategyName("Premium Equilibrium Discount")
@Layer1ApiVersion(Layer1ApiVersionValue.VERSION2)
public class EquilibriumAddon implements
        Layer1ApiFinishable,
        Layer1ApiAdminAdapter,
        Layer1ApiInstrumentAdapter {

    private static final String INDICATOR_NAME = "Premium/Equilibrium/Discount";

    // Line indicators
    private static final String EQUILIBRIUM_LINE = "Equilibrium";
    private static final String PREMIUM_HIGH = "Premium High";
    private static final String DISCOUNT_LOW = "Discount Low";

    // Zone colors
    private static final Color PREMIUM_COLOR = new Color(255, 100, 100, 60); // Red translucent
    private static final Color EQUILIBRIUM_COLOR = new Color(255, 255, 0, 80); // Yellow translucent
    private static final Color DISCOUNT_COLOR = new Color(100, 255, 100, 60); // Green translucent

    private Layer1ApiProvider provider;
    private Map<String, EquilibriumCalculator> calculators = new HashMap<>();
    private EquilibriumSettings settings;

    // Settings
    private int lookbackPeriod = 100;
    private boolean useSessionHighLow = true;
    private boolean showPremiumZone = true;
    private boolean showDiscountZone = true;
    private boolean showEquilibriumLine = true;

    @Override
    public void initialize(String alias, InstrumentInfo info, Layer1ApiProvider provider) {
        this.provider = provider;

        // Create calculator for this instrument
        EquilibriumCalculator calculator = new EquilibriumCalculator(
            lookbackPeriod,
            useSessionHighLow,
            info.pips
        );
        calculators.put(alias, calculator);

        // Register indicators
        registerIndicators(alias);

        Log.info("EquilibriumAddon initialized for " + alias);
    }

    @Override
    public void finish() {
        calculators.clear();
    }

    @Override
    public void onTrade(double price, int size, TradeInfo tradeInfo) {
        String alias = tradeInfo.alias;
        EquilibriumCalculator calculator = calculators.get(alias);

        if (calculator != null) {
            // Update calculator with new price
            calculator.addPrice(price, tradeInfo.time);

            // Calculate zones
            EquilibriumZones zones = calculator.calculateZones();

            // Update indicators
            updateIndicators(alias, zones);
        }
    }

    private void registerIndicators(String alias) {
        // Equilibrium line
        if (showEquilibriumLine) {
            Layer1ApiProvider.IndicatorColorScheme colorScheme =
                new Layer1ApiProvider.IndicatorColorScheme() {
                    @Override
                    public Color getColor() {
                        return Color.YELLOW;
                    }
                };

            provider.registerIndicator(
                EQUILIBRIUM_LINE,
                DataType.PRICE,
                colorScheme,
                Layer1ApiProvider.LineType.SOLID
            );
        }

        // Premium high line
        if (showPremiumZone) {
            Layer1ApiProvider.IndicatorColorScheme premiumScheme =
                new Layer1ApiProvider.IndicatorColorScheme() {
                    @Override
                    public Color getColor() {
                        return new Color(255, 100, 100);
                    }
                };

            provider.registerIndicator(
                PREMIUM_HIGH,
                DataType.PRICE,
                premiumScheme,
                Layer1ApiProvider.LineType.DASHED
            );
        }

        // Discount low line
        if (showDiscountZone) {
            Layer1ApiProvider.IndicatorColorScheme discountScheme =
                new Layer1ApiProvider.IndicatorColorScheme() {
                    @Override
                    public Color getColor() {
                        return new Color(100, 255, 100);
                    }
                };

            provider.registerIndicator(
                DISCOUNT_LOW,
                DataType.PRICE,
                discountScheme,
                Layer1ApiProvider.LineType.DASHED
            );
        }
    }

    private void updateIndicators(String alias, EquilibriumZones zones) {
        if (zones == null) return;

        long currentTime = System.currentTimeMillis();

        // Update equilibrium line
        if (showEquilibriumLine && !Double.isNaN(zones.equilibrium)) {
            provider.onIndicatorData(
                EQUILIBRIUM_LINE,
                alias,
                currentTime,
                zones.equilibrium
            );
        }

        // Update premium zone
        if (showPremiumZone && !Double.isNaN(zones.premiumHigh)) {
            provider.onIndicatorData(
                PREMIUM_HIGH,
                alias,
                currentTime,
                zones.premiumHigh
            );
        }

        // Update discount zone
        if (showDiscountZone && !Double.isNaN(zones.discountLow)) {
            provider.onIndicatorData(
                DISCOUNT_LOW,
                alias,
                currentTime,
                zones.discountLow
            );
        }
    }

    @Override
    public StrategyPanel[] getStrategyPanels() {
        if (settings == null) {
            settings = new EquilibriumSettings(this);
        }
        return new StrategyPanel[] { settings };
    }

    // Getters and setters for settings
    public int getLookbackPeriod() {
        return lookbackPeriod;
    }

    public void setLookbackPeriod(int lookbackPeriod) {
        this.lookbackPeriod = lookbackPeriod;
        // Update all calculators
        for (EquilibriumCalculator calc : calculators.values()) {
            calc.setLookbackPeriod(lookbackPeriod);
        }
    }

    public boolean isUseSessionHighLow() {
        return useSessionHighLow;
    }

    public void setUseSessionHighLow(boolean useSessionHighLow) {
        this.useSessionHighLow = useSessionHighLow;
        // Update all calculators
        for (EquilibriumCalculator calc : calculators.values()) {
            calc.setUseSessionHighLow(useSessionHighLow);
        }
    }

    public boolean isShowPremiumZone() {
        return showPremiumZone;
    }

    public void setShowPremiumZone(boolean showPremiumZone) {
        this.showPremiumZone = showPremiumZone;
    }

    public boolean isShowDiscountZone() {
        return showDiscountZone;
    }

    public void setShowDiscountZone(boolean showDiscountZone) {
        this.showDiscountZone = showDiscountZone;
    }

    public boolean isShowEquilibriumLine() {
        return showEquilibriumLine;
    }

    public void setShowEquilibriumLine(boolean showEquilibriumLine) {
        this.showEquilibriumLine = showEquilibriumLine;
    }
}
