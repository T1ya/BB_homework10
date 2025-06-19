package ait.cohort5860.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties prop = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}
