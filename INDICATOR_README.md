# Premium & Discount Delta Volume Indicator for Bookmap

## Overview

This indicator has been converted from your TradingView Pine Script to work with Bookmap's Java Layer 1 API.

**Key Improvements over TradingView version:**
- Uses **actual bid/ask trade data** (not approximated from candle close direction)
- Real-time tick-level precision
- Direct access to order flow and market microstructure
- More accurate delta volume calculations

---

## What It Does

### Two Timeframes:

**MACRO (200-period) - Intraday Swing Trades**
- Hold time: 30 minutes - 4 hours
- Delta threshold: 60%
- Visual: Thicker zone lines
- Best for: Trending days, opening range, power hour

**MICRO (50-period) - Scalp Trades**
- Hold time: 1-5 minutes
- Delta threshold: 30%
- Visual: Thinner zone lines
- Best for: Choppy mid-day, range-bound action

### Features:

1. **Premium & Discount Zones**: Visual boxes showing supply/demand areas
2. **Delta Volume**: Real bid/ask volume imbalance (NOT approximated!)
3. **Buy/Sell Signals**: Automated entry markers
4. **Equilibrium Lines**: Mid-point references
5. **Live Dashboard**: Real-time delta percentages

---

## File Location

```
DemoStrategies/Strategies/src/main/java/com/prestotrading/indicators/PremiumDiscountDeltaVolume.java
```

---

## Building the Indicator

### Prerequisites

1. **Java 17+** (check with `java --version`)
2. **Gradle** (check with `gradle --version`)
3. **Bookmap** installed
4. **Internet connection** (for downloading dependencies)

### Build Steps

```bash
# Navigate to the Strategies directory
cd DemoStrategies/Strategies

# Build the JAR file
gradle jar

# Output will be in:
# build/libs/bm-strategies.jar
```

---

## Loading into Bookmap

1. **Open Bookmap**
2. Go to **Settings → API plugins configuration** (or click the API toolbar button)
3. Click **"Add"**
4. Navigate to `DemoStrategies/Strategies/build/libs/bm-strategies.jar`
5. Select **"Premium & Discount Delta Volume"** from the popup
6. **Enable** the indicator using the checkbox

---

## How It Works (Technical Details)

### Delta Volume Calculation

**TradingView (Old Method):**
```
if close > open → Buy Volume
if close < open → Sell Volume
```

**Bookmap (New Method):**
```
if trade.isBidAggressor() → Sell Volume (market sell)
if trade.isAskAggressor() → Buy Volume (market buy)
```

**Result:** Much more accurate! We're using the actual aggressor side, not guessing from candle direction.

### Formula

```
Delta % = ((Avg Sell Volume / Avg Buy Volume) - 1) × 100
```

- **Positive Delta** = More selling pressure (look for discounts to buy)
- **Negative Delta** = More buying pressure (look for premiums to sell)

### Zone Calculation

```
MACRO Upper Zone = Max(highs over 200 bars) + (ATR × 0.8)
MACRO Lower Zone = Min(lows over 200 bars) - (ATR × 0.8)
MICRO Upper Zone = Max(highs over 50 bars) + (ATR × 0.8)
MICRO Lower Zone = Min(lows over 50 bars) - (ATR × 0.8)
```

---

## Signals

### MACRO BUY Signal
- Price <= MACRO Discount Zone
- Delta Volume > 60%
- **Action**: Enter LONG (intraday swing)
- **Hold**: 30 min - 4 hours

### MACRO SELL Signal
- Price >= MACRO Premium Zone
- Delta Volume < -60%
- **Action**: Enter SHORT (intraday swing)
- **Hold**: 30 min - 4 hours

### MICRO BUY Signal
- Price <= MICRO Discount Zone
- Delta Volume > 30%
- **Action**: Enter LONG (scalp)
- **Hold**: 1-5 minutes

### MICRO SELL Signal
- Price >= MICRO Premium Zone
- Delta Volume < -30%
- **Action**: Enter SHORT (scalp)
- **Hold**: 1-5 minutes

---

## Visual Elements

**On Primary Chart (Heatmap):**
- Blue lines = Discount zones (buy areas)
- Orange/red lines = Premium zones (sell areas)
- Dashed gray line = MICRO equilibrium
- Dotted dark gray line = MACRO equilibrium
- Triangle up = MACRO BUY signal
- Triangle down = MACRO SELL signal
- Circle = MICRO signals

**On Bottom Subchart:**
- Cyan line = MICRO Delta %
- Magenta line = MACRO Delta %

---

## Customization

To adjust thresholds or periods, edit these constants in the Java file:

```java
// Line 29-36
private static final int MICRO_PERIOD = 50;
private static final double MICRO_THRESHOLD = 30.0;
private static final int MACRO_PERIOD = 200;
private static final double MACRO_THRESHOLD = 60.0;
private static final Color DISCOUNT_COLOR = new Color(121, 193, 241);
private static final Color PREMIUM_COLOR = new Color(241, 149, 121);
```

After changes, rebuild with `gradle jar` and restart Bookmap.

---

## Troubleshooting

### Build Fails
- Check Java version: `java --version` (needs 17+)
- Check internet connection (needed for dependencies)
- Try: `gradle clean jar`

### Indicator Not Showing
- Verify JAR loaded in API plugins configuration
- Check the checkbox is enabled
- Look for errors in Bookmap console

### No Signals Appearing
- Wait for 200 bars to accumulate (for MACRO)
- Check that data feed is working
- Verify instrument has volume data

### Zones Look Wrong
- ATR needs time to calculate (200 bars)
- Check if instrument has sufficient history
- Try on 1-minute chart first

---

## Differences from TradingView Version

**What's Better:**
✅ Real bid/ask delta (not guessed from candle close)
✅ Tick-level precision
✅ Real-time updates (not just on bar close)
✅ Direct market microstructure access

**What's Different:**
- No visual boxes (uses zone lines instead - Bookmap limitation)
- No interactive table overlay (info shown in settings panel)
- Bars are time-based (1-minute) instead of chart timeframe
- Alerts go to Bookmap messages instead of TradingView alerts

**What's Missing (can be added if needed):**
- Custom settings UI (currently hardcoded values)
- Box drawing (requires additional Screen Space Painter API)
- Multi-timeframe support (currently 1-minute bars)

---

## Next Steps

1. **Build the indicator** using gradle
2. **Load into Bookmap** via API plugins
3. **Test on paper trading** first!
4. **Adjust thresholds** based on your instrument/timeframe
5. **Let me know** if you need any modifications or additional features

---

## Support

If you need to modify this indicator or add features, I can help with:
- Adding custom settings panels
- Drawing boxes instead of lines
- Multiple timeframe support
- Alert system integration
- Performance optimization
- Additional signal filters

Just let me know what you need!
