package savage.savdbcore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility for loading and saving JSON configuration files.
 */
public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Load a configuration object from a JSON file.
     * If the file doesn't exist, creates it with default values.
     * 
     * @param file The configuration file
     * @param configClass The configuration class type
     * @param <T> The configuration type
     * @return The loaded or default configuration
     */
    public static <T> T load(File file, Class<T> configClass) {
        return load(file.toPath(), configClass);
    }

    /**
     * Load a configuration object from a JSON file.
     * If the file doesn't exist, creates it with default values.
     * 
     * @param path The configuration file path
     * @param configClass The configuration class type
     * @param <T> The configuration type
     * @return The loaded or default configuration
     */
    public static <T> T load(Path path, Class<T> configClass) {
        try {
            // Create parent directories if they don't exist
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            // If file doesn't exist, create it with defaults
            if (!Files.exists(path)) {
                T defaultConfig = configClass.getDeclaredConstructor().newInstance();
                save(path, defaultConfig);
                return defaultConfig;
            }

            // Read and parse the file
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, configClass);
            }

        } catch (Exception e) {
            System.err.println("[SavDBCore] Failed to load config from " + path + ": " + e.getMessage());
            e.printStackTrace();
            
            // Return default config on error
            try {
                return configClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create default config", ex);
            }
        }
    }

    /**
     * Save a configuration object to a JSON file.
     * 
     * @param file The configuration file
     * @param config The configuration object to save
     */
    public static void save(File file, Object config) {
        save(file.toPath(), config);
    }

    /**
     * Save a configuration object to a JSON file.
     * 
     * @param path The configuration file path
     * @param config The configuration object to save
     */
    public static void save(Path path, Object config) {
        try {
            // Create parent directories if they don't exist
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            // Write the config
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }

        } catch (Exception e) {
            System.err.println("[SavDBCore] Failed to save config to " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the Gson instance used for serialization.
     * @return The Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }
}
