
package ru.sfedu.touragency.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility. Allows to get configuration properties from the
 * default configuration file
 *
 * @author Boris Jmailov
 */
public class ConfigurationUtil {

    private static final String DEFAULT_CONFIG_PATH = "/enviroment.properties";
    private static final Properties CONFIGURATION = new Properties();

    /**
     * Hides default constructor
     */
    public ConfigurationUtil() {
    }

    
    private static Properties getConfiguration() throws IOException {
        if(CONFIGURATION.isEmpty()){
            loadConfiguration();
        }
        return CONFIGURATION;
    }

    /**
     * Loads configuration from <code>DEFAULT_CONFIG_PATH</code>
     * @throws IOException In case of the configuration file read failure
     */
    private static void loadConfiguration() throws IOException {
        InputStream in = DEFAULT_CONFIG_PATH.getClass().getResourceAsStream(DEFAULT_CONFIG_PATH);
        try {
            CONFIGURATION.load(in);
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally{
            in.close();
        }
    }   
    /**
     * Gets configuration entry value
     * @param key Entry key
     * @return Entry value by key
     * @throws IOException In case of the configuration file read failure
     */
    public static String getConfigurationEntry(String key) throws IOException {
        return getConfiguration().getProperty(key);
    }
    
}