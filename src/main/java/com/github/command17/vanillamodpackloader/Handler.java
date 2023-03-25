package com.github.command17.vanillamodpackloader;

import com.github.command17.vanillamodpackloader.gui.LoaderGui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Handler {
    protected static final int HORIZONTAL_SPACING = 4;
    protected static final int VERTICAL_SPACING = 6;

    private JPanel pane;

    public JTextField minecraftLocation;
    public JButton selectFolderButton;
    public JTextField modpackLocation;
    public JButton selectFileButton;
    public JButton buttonInstall;
    public JLabel statusLabel;

    public abstract String name();

    public abstract void install();

    public void setupPane1(JPanel pane, GridBagConstraints c, LoaderGui loaderGui) {}

    public void setupPane2(JPanel pane, GridBagConstraints c, LoaderGui loaderGui) {}

    public JPanel makePanel(LoaderGui loaderGui) {
        pane = new JPanel(new GridBagLayout());
        pane.setBorder(new EmptyBorder(4, 4, 4, 4));

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.insets = new Insets(VERTICAL_SPACING, HORIZONTAL_SPACING, VERTICAL_SPACING, HORIZONTAL_SPACING);
        constraints.gridx = constraints.gridy = 0;

        setupPane1(pane, constraints, loaderGui);

        addRow(pane, constraints, "Minecraft Location",
                minecraftLocation = new JTextField(20),
                selectFolderButton = new JButton());

        minecraftLocation.setText(Main.getMinecraftDir().getAbsolutePath());

        addRow(pane, constraints, "VanillaPack Location",
                modpackLocation = new JTextField(20),
                selectFileButton = new JButton());

        selectFolderButton.setText("...");
        selectFolderButton.setPreferredSize(new Dimension(minecraftLocation.getPreferredSize().height, minecraftLocation.getPreferredSize().height));
        selectFolderButton.addActionListener(e -> LoaderGui.selectMinecraftLocation(() -> minecraftLocation.getText(), s -> minecraftLocation.setText(s)));

        selectFileButton.setText("...");
        selectFileButton.setPreferredSize(new Dimension(modpackLocation.getPreferredSize().height, modpackLocation.getPreferredSize().height));
        selectFileButton.addActionListener(e -> LoaderGui.selectVanillaPackLocation(() -> modpackLocation.getText(), s -> modpackLocation.setText(s)));

        setupPane2(pane, constraints, loaderGui);

        addRow(pane, constraints, null,
                statusLabel = new JLabel());

        statusLabel.setPreferredSize(new Dimension(modpackLocation.getPreferredSize().width, modpackLocation.getPreferredSize().height));
        statusLabel.setMaximumSize(new Dimension(modpackLocation.getMaximumSize().width, modpackLocation.getMaximumSize().height));
        statusLabel.setMinimumSize(new Dimension(modpackLocation.getMinimumSize().width, modpackLocation.getMinimumSize().height));
        statusLabel.setVerticalAlignment(JLabel.CENTER);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setVerticalTextPosition(JLabel.CENTER);
        statusLabel.setHorizontalTextPosition(JLabel.CENTER);

        addLastRow(pane, constraints , null,
                buttonInstall = new JButton("Install"));

        buttonInstall.addActionListener(event -> {
            buttonInstall.setEnabled(false);

            install();
        });

        return pane;
    }

    protected void addRow(Container parent, GridBagConstraints c, String label, Component... components) {
        addRow(parent, c, false, label, components);
    }

    protected void addLastRow(Container parent, GridBagConstraints c, String label, Component... components) {
        addRow(parent, c, true, label, components);
    }

    protected static Component createSpacer() {
        return Box.createRigidArea(new Dimension(4, 0));
    }

    private void addRow(Container parent, GridBagConstraints c, boolean last, String label, Component... components) {
        if (label != null) {
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.LINE_END;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 0;

            parent.add(new JLabel(label), c);

            c.gridx++;
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
        } else {
            c.gridwidth = 2;

            if (last) c.weighty = 1;

            c.anchor = last ? GridBagConstraints.PAGE_START : GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
        }

        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        for (Component comp: components) {
            panel.add(comp);
        }

        parent.add(panel, c);

        c.gridy++;
        c.gridx = 0;
    }

    public void error(String error) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(error);

        statusLabel.paintImmediately(statusLabel.getVisibleRect());

        System.out.println(error);
    }

    public void status(String status) {
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText(status);

        statusLabel.paintImmediately(statusLabel.getVisibleRect());

        System.out.println(status);
    }
}