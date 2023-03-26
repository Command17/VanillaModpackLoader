package com.github.command17.vanillamodpackloader.handler;

import com.github.command17.vanillamodpackloader.Handler;
import com.github.command17.vanillamodpackloader.util.FileUtil;
import com.moandjiezana.toml.Toml;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModpackHandler extends Handler {
    @Override
    public String name() {
        return "Modpack";
    }

    @Override
    public void install() {
        String modsPath = Path.of(minecraftLocation.getText(), "mods").toAbsolutePath().toString();

        File modsFolder = new File(modsPath);

        status("Installing");

        if (modsFolder.isDirectory()) {
            File modpackToml = new File(modpackLocation.getText());

            if (modpackToml.exists() && modpackLocation.getText().endsWith(".toml")) {
                String source = null;
                
                try {
                    source = FileUtil.fastReadFile(modpackLocation.getText());
                } catch (Exception e) {
                    error("Error while reading file!");
                }
                
                if (source != null) {
                    // Clearing

                    File[] remainingFilesToDel = modsFolder.listFiles();

                    // Clears remaining files from the mods folder
                    for (File file: remainingFilesToDel) {
                        if (file.exists() && !file.isDirectory()) {
                            status("Deleting " + file.getAbsolutePath() + "...");

                            file.delete();
                        }
                    }

                    status("Cleared " + modsPath);

                    // Main Installation

                    Toml toml = new Toml().read(source);

                    // Mods Installation

                    @Nullable String modZip = toml.getString("modpack.modZip.zip");

                    @Nullable Toml modTable = toml.getTable("modpack.mods");

                    // Install Mods from Array

                    Random rng = new Random();

                    if (modTable != null && !modTable.isEmpty()) {
                        for (Map.Entry<String, Object> modEntry: modTable.entrySet()) {
                            try {
                                InputStream in = new URL(modEntry.getValue().toString()).openStream();

                                String path = Path.of(modsPath, modEntry.getKey() + rng.nextInt(0, 4000) + ".jar").toAbsolutePath().toString();

                                Files.copy(in, Path.of(path));

                                status("Installed " + modEntry.getValue().toString());
                            } catch (Exception e) {
                                error("Unable to download " + modEntry.getValue().toString());
                            }
                        }
                    }

                    // Install Mods from Zip

                    try {
                        status("Downloading Zip...");

                        InputStream in = new URL(modZip).openStream();

                        String path = Path.of(System.getProperty("java.io.tmpdir"), "mods" + rng.nextInt(0, 400) + ".zip").toAbsolutePath().toString();

                        Files.copy(in, Path.of(path));

                        ZipFile file = new ZipFile(path);

                        if (file.isEncrypted()) {
                            error("Zip File is Encrypted.");

                            throw new ZipException("Encrypted");
                        }

                        status("Extracting Zip...");

                        file.extractAll(modsPath);
                    } catch (Exception e) {
                        error("Unable to download " + modZip);
                    }

                    // Mod Loader Installation

                    String loaderName = toml.getString("modpack.loader.name");
                    String loaderMcVersion = toml.getString("modpack.loader.mc_version");
                    String loaderVersion = toml.getString("modpack.loader.version");

                    if (loaderName != null && loaderMcVersion != null && loaderVersion != null && !loaderName.isEmpty() && !loaderMcVersion.isEmpty() && !loaderVersion.isEmpty()) {
                        switch (loaderName) {
                            case "forge": // https://maven.minecraftforge.net/net/minecraftforge/forge/mc_version-version/forge-mc_version-version-installer.jar
                                status("Downloading Forge...");

                                try {
                                    InputStream in = new URL(String.format(
                                            "https://maven.minecraftforge.net/net/minecraftforge/forge/%s-%s/forge-%s-%s-installer.jar",
                                            loaderMcVersion, loaderVersion, loaderMcVersion, loaderVersion)).openStream();

                                    String installerPath = Path.of(System.getProperty("java.io.tmpdir"), "forge-installer.jar").toAbsolutePath().toString();

                                    Files.copy(in, Paths.get(installerPath), StandardCopyOption.REPLACE_EXISTING);

                                    Runtime.getRuntime().exec("java -jar " + installerPath);

                                    File forgeInstaller = new File(installerPath);
                                    File log = new File(installerPath + ".log");

                                    if (forgeInstaller.exists()) forgeInstaller.deleteOnExit();
                                    if (log.exists()) log.deleteOnExit();
                                    else {
                                        log = new File("forge-installer.jar.log");

                                        if (log.exists()) log.deleteOnExit();
                                    }

                                    status("Installed Forge");
                                } catch (Exception e) {
                                    error("Error while downloading forge!");
                                }

                                break;

                            case "fabric": // https://maven.fabricmc.net/net/fabricmc/fabric-installer/version/fabric-installer-version.jar
                                status("Downloading Fabric...");

                                try {
                                    InputStream in = new URL(String.format(
                                            "https://maven.fabricmc.net/net/fabricmc/fabric-installer/%s/fabric-installer-%s.jar",
                                            loaderVersion, loaderVersion)).openStream();

                                    String installerPath = Path.of(System.getProperty("java.io.tmpdir"), "fabric-installer.jar").toAbsolutePath().toString();

                                    Files.copy(in, Paths.get(installerPath), StandardCopyOption.REPLACE_EXISTING);

                                    String launcherFlag = null;

                                    if (win32Launcher.isSelected()) launcherFlag = "win32";
                                    else launcherFlag = "microsoft_store";

                                    Runtime.getRuntime().exec("java -jar " + installerPath + " client -mcversion " + loaderMcVersion + " -dir " + minecraftLocation.getText() + " -launcher " + launcherFlag); // I will assume that everyone uses microsoft_store

                                    File fabricInstaller = new File(installerPath);

                                    if (fabricInstaller.exists()) fabricInstaller.deleteOnExit();

                                    status("Installed Fabric");
                                } catch (Exception e) {
                                    error("Error while downloading fabric!");
                                }

                                break;

                            case "quilt": // https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-installer/version/quilt-installer-version.jar
                                status("Downloading Quilt...");

                                try {
                                    InputStream in = new URL(String.format(
                                            "https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-installer/%s/quilt-installer-%s.jar",
                                            loaderVersion, loaderVersion)).openStream();

                                    String installerPath = Path.of(System.getProperty("java.io.tmpdir"), "fabric-installer.jar").toAbsolutePath().toString();

                                    Files.copy(in, Paths.get(installerPath), StandardCopyOption.REPLACE_EXISTING);

                                    Runtime.getRuntime().exec("java -jar " + installerPath + " client " + loaderMcVersion + " --install-dir=" + minecraftLocation.getText());

                                    File fabricInstaller = new File(installerPath);

                                    if (fabricInstaller.exists()) fabricInstaller.deleteOnExit();

                                    status("Installed Quilt");
                                } catch (Exception e) {
                                    error("Error while downloading quilt!");
                                }

                                break;

                            default:
                                error("Unknown Mod Loader!");

                                break;
                        }
                    } else {
                        error("Loader property is missing or is empty!");
                    }
                }
            } else {
                error("Modpack.toml does not exist!");
            }
        } else {
            error(modsPath + " does not exist or is not a directory!");
        }

        if (!statusLabel.getForeground().equals(Color.RED)) {
            status("Done");
        }

        buttonInstall.setEnabled(true);
    }
}
