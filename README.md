# Bookmap Premium Equilibrium Discount Addon

A powerful Bookmap addon that displays **Premium**, **Equilibrium**, and **Discount** zones on your trading charts, helping you identify optimal entry and exit points based on price structure.

## Features

- **Equilibrium Line**: Displays the fair value price (midpoint between high and low)
- **Premium Zone**: Shows areas where price is expensive (above equilibrium)
- **Discount Zone**: Shows areas where price is cheap (below equilibrium)
- **Flexible Calculation Methods**:
  - Session-based (24-hour high/low)
  - Lookback period-based (configurable number of bars)
- **Customizable Display**: Toggle individual zones on/off
- **Real-time Updates**: Zones update automatically as price action develops

## Concept

The addon implements a trading concept where:

- **Premium Zone** (Red): Price is trading above equilibrium - potentially expensive, good for selling/shorting
- **Equilibrium** (Yellow): Fair value price - balanced, neutral zone
- **Discount Zone** (Green): Price is trading below equilibrium - potentially cheap, good for buying/longing

This helps traders:
- Identify value areas in the market
- Make better entry/exit decisions
- Understand current price structure
- Avoid chasing price in premium or discount extremes

## Installation

### Prerequisites

- Bookmap 7.4 or higher
- Java 11 or higher
- Maven 3.6+ (for building from source)

### Building from Source

1. Clone this repository:
   ```bash
   git clone <repository-url>
   cd prestotrading
   ```

2. Build the project with Maven:
   ```bash
   mvn clean package
   ```

3. The compiled JAR will be in `target/equilibrium-addon-1.0.0.jar`

### Installing in Bookmap

1. Copy the JAR file to your Bookmap addons directory:
   - **Windows**: `C:\Program Files\Bookmap\Config\Addons`
   - **Mac**: `~/Library/Application Support/Bookmap/Config/Addons`
   - **Linux**: `~/.bookmap/Config/Addons`

2. Restart Bookmap

3. Right-click on a chart → **Add Indicator** → Select "Premium Equilibrium Discount"

## Usage

### Basic Setup

1. Add the indicator to your chart
2. The addon will immediately start displaying:
   - Yellow line: Equilibrium (midpoint)
   - Red dashed line: Premium high
   - Green dashed line: Discount low

### Configuration

Access the settings panel to customize:

#### Lookback Period
- **Range**: 10 - 1000 bars
- **Default**: 100 bars
- **Purpose**: Determines how many historical bars to use for calculating high/low

#### Use Session High/Low
- **When enabled**: Uses 24-hour session high/low for zones
- **When disabled**: Uses the lookback period for high/low calculation
- **Tip**: Enable for swing trading, disable for intraday scalping

#### Display Options
- **Show Premium Zone**: Toggle premium (high) line
- **Show Equilibrium Line**: Toggle equilibrium line
- **Show Discount Zone**: Toggle discount (low) line

### Trading Applications

#### Buy/Long Setups
- Look for entries in the **discount zone** (green)
- Price in discount = potentially undervalued
- Target: Equilibrium or premium zone

#### Sell/Short Setups
- Look for entries in the **premium zone** (red)
- Price in premium = potentially overvalued
- Target: Equilibrium or discount zone

#### Range Trading
- Buy at discount, sell at premium
- Equilibrium acts as pivot point
- Watch for breakouts beyond zones

## Technical Details

### Calculation Method

**Equilibrium Formula:**
```
Equilibrium = (High + Low) / 2
```

Where:
- High = Session high or lookback period high
- Low = Session low or lookback period low

**Premium Zone:**
```
Premium = Equilibrium to High
```

**Discount Zone:**
```
Discount = Low to Equilibrium
```

### Architecture

```
com.bookmap.addons.equilibrium/
├── EquilibriumAddon.java         # Main addon class
├── EquilibriumCalculator.java    # Zone calculation logic
├── EquilibriumZones.java          # Data model for zones
└── EquilibriumSettings.java       # Settings UI panel
```

### API Integration

- Uses Bookmap Layer1 API v2
- Implements `Layer1ApiInstrumentAdapter` for trade data
- Registers indicators via `Layer1ApiProvider`
- Updates in real-time on every trade

## Configuration Examples

### Scalping Setup
- Lookback Period: 20-50 bars
- Use Session High/Low: **Disabled**
- Good for: Fast-moving markets, quick trades

### Day Trading Setup
- Lookback Period: 100-200 bars
- Use Session High/Low: **Enabled**
- Good for: Intraday swings, session-based trading

### Swing Trading Setup
- Lookback Period: 500-1000 bars
- Use Session High/Low: **Enabled**
- Good for: Multi-day positions, higher timeframes

## Troubleshooting

### Addon Not Appearing
- Verify JAR is in correct Bookmap addons directory
- Check Bookmap version (requires 7.4+)
- Restart Bookmap completely

### Lines Not Displaying
- Check display options in settings panel
- Ensure instrument has sufficient price data
- Verify lookback period is appropriate for timeframe

### Zones Not Updating
- Confirm addon is active on chart
- Check that market data is flowing
- Try adjusting lookback period

## Performance

- **Memory Usage**: Minimal (~10MB per instrument)
- **CPU Usage**: Low (updates only on trade events)
- **Latency**: Real-time (< 1ms calculation time)

## Version History

### v1.0.0 (2025-11-08)
- Initial release
- Core premium/equilibrium/discount functionality
- Session and lookback period modes
- Configurable display options
- Real-time zone updates

## Support

For issues, questions, or feature requests, please open an issue on GitHub.

## License

MIT License - see LICENSE file for details

## Acknowledgments

Built for the Bookmap trading platform using the official Bookmap API.

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

**Disclaimer**: This addon is for educational and informational purposes only. Trading involves risk. Always do your own research and consult with financial professionals before making trading decisions.
