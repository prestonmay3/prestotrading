package com.bookmap.addons.equilibrium;

import velox.gui.StrategyPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Settings panel for the Equilibrium addon.
 * Allows users to configure lookback period, zone display, and calculation method.
 */
public class EquilibriumSettings extends StrategyPanel {

    private final EquilibriumAddon addon;

    // UI Components
    private JSpinner lookbackSpinner;
    private JCheckBox sessionHighLowCheckbox;
    private JCheckBox showPremiumCheckbox;
    private JCheckBox showDiscountCheckbox;
    private JCheckBox showEquilibriumCheckbox;

    public EquilibriumSettings(EquilibriumAddon addon) {
        super("Premium/Equilibrium/Discount Settings");
        this.addon = addon;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main panel with settings
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Lookback Period Setting
        JPanel lookbackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lookbackPanel.add(new JLabel("Lookback Period:"));
        lookbackSpinner = new JSpinner(new SpinnerNumberModel(
            addon.getLookbackPeriod(), // initial value
            10,    // min
            1000,  // max
            10     // step
        ));
        lookbackSpinner.setPreferredSize(new Dimension(100, 25));
        lookbackSpinner.addChangeListener(e -> {
            addon.setLookbackPeriod((Integer) lookbackSpinner.getValue());
        });
        lookbackPanel.add(lookbackSpinner);
        lookbackPanel.add(new JLabel("bars"));
        mainPanel.add(lookbackPanel);

        // Session High/Low Checkbox
        sessionHighLowCheckbox = new JCheckBox("Use Session High/Low", addon.isUseSessionHighLow());
        sessionHighLowCheckbox.addActionListener(e -> {
            addon.setUseSessionHighLow(sessionHighLowCheckbox.isSelected());
        });
        mainPanel.add(sessionHighLowCheckbox);

        // Separator
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Display Options Label
        JLabel displayLabel = new JLabel("Display Options:");
        displayLabel.setFont(displayLabel.getFont().deriveFont(Font.BOLD));
        mainPanel.add(displayLabel);

        // Show Premium Zone Checkbox
        showPremiumCheckbox = new JCheckBox("Show Premium Zone", addon.isShowPremiumZone());
        showPremiumCheckbox.addActionListener(e -> {
            addon.setShowPremiumZone(showPremiumCheckbox.isSelected());
        });
        mainPanel.add(showPremiumCheckbox);

        // Show Equilibrium Line Checkbox
        showEquilibriumCheckbox = new JCheckBox("Show Equilibrium Line", addon.isShowEquilibriumLine());
        showEquilibriumCheckbox.addActionListener(e -> {
            addon.setShowEquilibriumLine(showEquilibriumCheckbox.isSelected());
        });
        mainPanel.add(showEquilibriumCheckbox);

        // Show Discount Zone Checkbox
        showDiscountCheckbox = new JCheckBox("Show Discount Zone", addon.isShowDiscountZone());
        showDiscountCheckbox.addActionListener(e -> {
            addon.setShowDiscountZone(showDiscountCheckbox.isSelected());
        });
        mainPanel.add(showDiscountCheckbox);

        // Separator
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JSeparator());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Legend"));

        JLabel premiumLabel = new JLabel("Premium Zone: Price above equilibrium (expensive)");
        premiumLabel.setForeground(new Color(255, 100, 100));
        infoPanel.add(premiumLabel);

        JLabel equilibriumLabel = new JLabel("Equilibrium: Fair value / balanced price");
        equilibriumLabel.setForeground(Color.YELLOW.darker());
        infoPanel.add(equilibriumLabel);

        JLabel discountLabel = new JLabel("Discount Zone: Price below equilibrium (cheap)");
        discountLabel.setForeground(new Color(100, 200, 100));
        infoPanel.add(discountLabel);

        mainPanel.add(infoPanel);

        // Add main panel to center
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void onSettingsUpdated() {
        // Update UI components if settings changed externally
        lookbackSpinner.setValue(addon.getLookbackPeriod());
        sessionHighLowCheckbox.setSelected(addon.isUseSessionHighLow());
        showPremiumCheckbox.setSelected(addon.isShowPremiumZone());
        showEquilibriumCheckbox.setSelected(addon.isShowEquilibriumLine());
        showDiscountCheckbox.setSelected(addon.isShowDiscountZone());
    }
}
