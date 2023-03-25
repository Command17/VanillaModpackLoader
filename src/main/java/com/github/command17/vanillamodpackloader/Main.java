package com.github.command17.vanillamodpackloader;

import com.github.command17.vanillamodpackloader.gui.LoaderGui;
import com.github.command17.vanillamodpackloader.handler.ModpackHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    public static final List<Handler> HANDLERS = new ArrayList<>();

    public static void main(String[] args) {
        HANDLERS.add(new ModpackHandler());

        try {
            LoaderGui.start();
        } catch (Exception e) {
            System.out.println("Something went wrong when starting!");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public static File getMinecraftDir() {
        String userHomeDir = System.getProperty("user.home", ".");

        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        String mcDir = ".minecraft";

        if (osType.contains("win") && System.getenv("APPDATA") != null) return new File(System.getenv("APPDATA"), mcDir);
        else if (osType.contains("mac")) return new File(new File(new File(userHomeDir, "Library"),"Application Support"),"minecraft");

        return new File(userHomeDir, mcDir);
    }
}