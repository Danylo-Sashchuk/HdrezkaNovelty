package org.parser.util;

import org.parser.models.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
    private static final Logger LOG = Logger.getLogger(Parser.class.getName());
    private static final File DEFAULT_CONFIG_FILE =
            new File("src/main/resources/defaultSuggester.properties");
    private static final Config INSTANCE = new Config();
    private final Properties prop = new Properties();

    private Config() {
        LOG.info("Create configs");
        try (InputStream input = new FileInputStream(DEFAULT_CONFIG_FILE)) {
            prop.load(input);
        } catch (IOException e) {
            LOG.severe("Properties has not been read\n" + e.getCause() + "\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
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
