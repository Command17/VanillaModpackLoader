package com.github.command17.vanillamodpackloader.util;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtil {
    @Nullable
    public static String fastReadFile(String path, boolean create) throws Exception {
        String source = null;

        try {
            File file = Paths.get(path).toFile();

            if (!file.exists()) {
                System.out.println("File " + path + " does not exist!");

                if (create) {
                    System.out.println("Creating " + path + "...");

                    if (!file.createNewFile()) {
                        System.out.println("Could not create " + path + "!");

                        return null;
                    }

                    System.out.println("Created " + path + ".");

                    Files.write(file.toPath(), source.getBytes());
                }
            } else {
                source = new String(Files.readAllBytes(file.toPath()));
            }
        } catch (Exception e) {
            System.out.println("File not found!");
            System.out.println(Arrays.toString(e.getStackTrace()));

            throw new Exception(Arrays.toString(e.getStackTrace()));
        }

        return source;
    }

    @Nullable
    public static String fastReadFile(String path) throws Exception {
        return fastReadFile(path, false);
    }
}
