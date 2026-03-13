package com.crm.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton config manager. Loads config.properties and supports
 * system property overrides (useful for CI/CD parameterization).
 */
public class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        String env = System.getProperty("env", "qa");
        String configFile = "src/test/resources/config-" + env + ".properties";
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            log.info("Loaded config for environment: {}", env);
        } catch (IOException e) {
            // Fallback to default config
            try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
                properties.load(fis);
                log.warn("Env config not found, loaded default config.properties");
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load configuration file", ex);
            }
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    /** Returns property value; system property takes precedence over file value. */
    public String get(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public boolean getBool(String key, boolean defaultValue) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the value for {@code key}, throwing {@link IllegalStateException}
     * if it is null or blank. Use this for required config (base URL, credentials).
     */
    public String getRequired(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Required config property '" + key + "' is not set. "
                + "Check config.properties or pass -D" + key + "=<value>");
        }
        return value;
    }

    // ─── Convenience getters ────────────────────────────────────────────────

    public String getBaseUrl()       { return getRequired("base.url"); }
    public String getBrowser()       { return get("browser", "chrome"); }
    public boolean isHeadless()      { return getBool("headless", false); }
    public String getAdminUser()     { return getRequired("admin.username"); }
    public String getAdminPass()     { return getRequired("admin.password"); }
    public int getExplicitWait()     { return getInt("explicit.wait", 15); }
    public String getReportDir()     { return get("report.dir", "target/extent-reports"); }
    public String getScreenshotDir() { return get("screenshot.dir", "target/screenshots"); }
}
