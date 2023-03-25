package com.github.command17.vanillamodpackloader.gui;

import com.github.command17.vanillamodpackloader.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoaderGui extends JFrame {
    public static LoaderGui instance;

    private JTabbedPane contentPane;

    public LoaderGui() throws IOException {
        initComponents();
        setContentPane(contentPane);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemClassLoader().getResource("icon.png")));

        instance = this;
    }

    public static void selectMinecraftLocation(Supplier<String> installDir, Consumer<String> selectedDir) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(Main.getMinecraftDir());
        fileChooser.setDialogTitle("Minecraft Location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedDir.accept(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public static void selectVanillaPackLocation(Supplier<String> installDir, Consumer<String> selectedDir) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File(installDir.get()));
        fileChooser.setDialogTitle("VanillaPack Location");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedDir.accept(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public static void start() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        String lafCls = UIManager.getSystemLookAndFeelClassName();

        UIManager.setLookAndFeel(lafCls);

        if (lafCls.endsWith("AquaLookAndFeel")) {
            UIManager.put("TabbedPane.foreground", Color.BLACK);
        }

        LoaderGui loaderGui = new LoaderGui();

        loaderGui.updateSize(true);
        loaderGui.setTitle("Vanilla Modpack Loader");
        loaderGui.setLocationRelativeTo(null);
        loaderGui.setVisible(true);
    }

    public void updateSize(boolean updateMinimum) {
        if (updateMinimum) setMinimumSize(null);

        setPreferredSize(null);
        pack();

        Dimension size = getPreferredSize();

        if (updateMinimum) setMinimumSize(size);

        setPreferredSize(new Dimension(Math.max(450, size.width), size.height));
        setSize(getPreferredSize());
    }

    private void initComponents() {
        contentPane = new JTabbedPane(JTabbedPane.TOP);

        Main.HANDLERS.forEach(handler -> contentPane.addTab(handler.name(), handler.makePanel(this)));
    }
}
