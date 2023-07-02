package org.suggester.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final File DEFAULT_CONFIG_FILE = new File(System.getProperty("user.dir") + "/src/main/resources" +
                                                             "/suggester.properties");
    private static final Config INSTANCE = new Config();
    private final Properties prop = new Properties();

    private Config() {
        try (InputStream input = new FileInputStream(DEFAULT_CONFIG_FILE)) {
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Properties has not been read", e);
        }
    }

    public static Config get() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
