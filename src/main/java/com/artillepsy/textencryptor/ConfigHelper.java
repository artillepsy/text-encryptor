package com.artillepsy.textencryptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ConfigHelper {
    private static final String CONFIG_FILE = ".config";
    private static final byte XOR_MASK = 0x42;
    /**
     * Reads the keyphrase from config.txt if it exists.
     */
    public static String loadKeyphrase() {
        Path path = Paths.get(CONFIG_FILE);
        if (Files.exists(path)) {
            try {
                var encoded = Files.readString(path).trim();
                return deobfuscate(encoded);
            } catch (IOException e) {
                System.err.println("Could not read config file: " + e.getMessage());
            }
        }
        return "";
    }

    /**
     * Saves or updates the keyphrase in config.txt.
     */
    public static void saveKeyphrase(String keyphrase) {
        try {
            var encoded = obfuscate(keyphrase);
            Files.writeString(Paths.get(CONFIG_FILE), encoded);
        } catch (IOException e) {
            System.err.println("Could not save config file: " + e.getMessage());
        }
    }

    /**
     * XORs the string and Base64-encodes it.
     */
    private static String obfuscate(String s) {
        byte[] bytes = s.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= XOR_MASK;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64-decodes and XORs the string back.
     */
    private static String deobfuscate(String s) {
        byte[] bytes = Base64.getDecoder().decode(s);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= XOR_MASK;
        }
        return new String(bytes);
    }
}
