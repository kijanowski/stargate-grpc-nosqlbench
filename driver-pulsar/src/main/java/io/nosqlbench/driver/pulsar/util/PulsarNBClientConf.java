package io.nosqlbench.driver.pulsar.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PulsarNBClientConf {

    private final static Logger logger = LogManager.getLogger(PulsarNBClientConf.class);

    private String canonicalFilePath = "";

    public static final String SCHEMA_CONF_PREFIX = "schema";
    public static final String CLIENT_CONF_PREFIX = "client";
    public static final String PRODUCER_CONF_PREFIX = "producer";
    public static final String CONSUMER_CONF_PREFIX = "consumer";
    public static final String READER_CONF_PREFIX = "reader";
    private final HashMap<String, Object> schemaConfMap = new HashMap<>();
    private final HashMap<String, Object> clientConfMap = new HashMap<>();
    private final HashMap<String, Object> producerConfMap = new HashMap<>();
    private final HashMap<String, Object> consumerConfMap = new HashMap<>();
    private final HashMap<String, Object> readerConfMap = new HashMap<>();
    // TODO: add support for other operation types: websocket-producer, managed-ledger

    public PulsarNBClientConf(String fileName) {
        File file = new File(fileName);

        try {
            canonicalFilePath = file.getCanonicalPath();

            Parameters params = new Parameters();

            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(params.properties()
                        .setFileName(fileName));

            Configuration config = builder.getConfiguration();

            // Get schema specific configuration settings
            for (Iterator<String> it = config.getKeys(SCHEMA_CONF_PREFIX); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();
                if (!StringUtils.isBlank(confVal))
                    schemaConfMap.put(confKey.substring(SCHEMA_CONF_PREFIX.length() + 1), config.getProperty(confKey));
            }

            // Get client connection specific configuration settings
            for (Iterator<String> it = config.getKeys(CLIENT_CONF_PREFIX); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();
                if (!StringUtils.isBlank(confVal))
                    clientConfMap.put(confKey.substring(CLIENT_CONF_PREFIX.length() + 1), config.getProperty(confKey));
            }

            // Get producer specific configuration settings
            for (Iterator<String> it = config.getKeys(PRODUCER_CONF_PREFIX); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();
                if (!StringUtils.isBlank(confVal))
                    producerConfMap.put(confKey.substring(PRODUCER_CONF_PREFIX.length() + 1), config.getProperty(confKey));
            }

            // Get consumer specific configuration settings
            for (Iterator<String> it = config.getKeys(CONSUMER_CONF_PREFIX); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();
                if (!StringUtils.isBlank(confVal))
                    consumerConfMap.put(confKey.substring(CONSUMER_CONF_PREFIX.length() + 1), config.getProperty(confKey));
            }

            // Get reader specific configuration settings
            for (Iterator<String> it = config.getKeys(READER_CONF_PREFIX); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();
                if (!StringUtils.isBlank(confVal))
                    readerConfMap.put(confKey.substring(READER_CONF_PREFIX.length() + 1), config.getProperty(confKey));
            }
        } catch (IOException ioe) {
            logger.error("Can't read the specified config properties file!");
            ioe.printStackTrace();
        } catch (ConfigurationException cex) {
            logger.error("Error loading configuration items from the specified config properties file: " + canonicalFilePath);
            cex.printStackTrace();
        }
    }


    //////////////////
    // Get Schema related config
    public Map<String, Object> getSchemaConfMap() {
        return this.schemaConfMap;
    }
    public boolean hasSchemaConfKey(String key) {
        if (key.contains(SCHEMA_CONF_PREFIX))
            return schemaConfMap.containsKey(key.substring(SCHEMA_CONF_PREFIX.length() + 1));
        else
            return schemaConfMap.containsKey(key);
    }
    public Object getSchemaConfValue(String key) {
        if (key.contains(SCHEMA_CONF_PREFIX))
            return schemaConfMap.get(key.substring(SCHEMA_CONF_PREFIX.length()+1));
        else
            return schemaConfMap.get(key);
    }
    public void setSchemaConfValue(String key, Object value) {
        if (key.contains(SCHEMA_CONF_PREFIX))
            schemaConfMap.put(key.substring(SCHEMA_CONF_PREFIX.length() + 1), value);
        else
            schemaConfMap.put(key, value);
    }


    //////////////////
    // Get Pulsar client related config
    public Map<String, Object> getClientConfMap() {
        return this.clientConfMap;
    }
    public boolean hasClientConfKey(String key) {
        if (key.contains(CLIENT_CONF_PREFIX))
            return clientConfMap.containsKey(key.substring(CLIENT_CONF_PREFIX.length() + 1));
        else
            return clientConfMap.containsKey(key);
    }
    public Object getClientConfValue(String key) {
        if (key.contains(CLIENT_CONF_PREFIX))
            return clientConfMap.get(key.substring(CLIENT_CONF_PREFIX.length()+1));
        else
            return clientConfMap.get(key);
    }
    public void setClientConfValue(String key, Object value) {
        if (key.contains(CLIENT_CONF_PREFIX))
            clientConfMap.put(key.substring(CLIENT_CONF_PREFIX.length() + 1), value);
        else
            clientConfMap.put(key, value);
    }


    //////////////////
    // Get Pulsar producer related config
    public Map<String, Object> getProducerConfMap() {
        return this.producerConfMap;
    }
    public boolean hasProducerConfKey(String key) {
        if (key.contains(PRODUCER_CONF_PREFIX))
            return producerConfMap.containsKey(key.substring(PRODUCER_CONF_PREFIX.length() + 1));
        else
            return producerConfMap.containsKey(key);
    }
    public Object getProducerConfValue(String key) {
        if (key.contains(PRODUCER_CONF_PREFIX))
            return producerConfMap.get(key.substring(PRODUCER_CONF_PREFIX.length()+1));
        else
            return producerConfMap.get(key);
    }
    public void setProducerConfValue(String key, Object value) {
        if (key.contains(PRODUCER_CONF_PREFIX))
            producerConfMap.put(key.substring(PRODUCER_CONF_PREFIX.length()+1), value);
        else
            producerConfMap.put(key, value);
    }
    // other producer helper functions ...
    public String getProducerName() {
        Object confValue = getProducerConfValue("producer.producerName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getProducerTopicName() {
        Object confValue = getProducerConfValue("producer.topicName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }


    //////////////////
    // Get Pulsar consumer related config
    public Map<String, Object> getConsumerConfMap() {
        return this.consumerConfMap;
    }
    public boolean hasConsumerConfKey(String key) {
        if (key.contains(CONSUMER_CONF_PREFIX))
            return consumerConfMap.containsKey(key.substring(CONSUMER_CONF_PREFIX.length() + 1));
        else
            return consumerConfMap.containsKey(key);
    }
    public Object getConsumerConfValue(String key) {
        if (key.contains(CONSUMER_CONF_PREFIX))
            return consumerConfMap.get(key.substring(CONSUMER_CONF_PREFIX.length() + 1));
        else
            return consumerConfMap.get(key);
    }
    public void setConsumerConfValue(String key, Object value) {
        if (key.contains(CONSUMER_CONF_PREFIX))
            consumerConfMap.put(key.substring(CONSUMER_CONF_PREFIX.length() + 1), value);
        else
            consumerConfMap.put(key, value);
    }
    // Other consumer helper functions ...
    public String getConsumerTopicNames() {
        Object confValue = getConsumerConfValue("consumer.topicNames");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getConsumerTopicPattern() {
        Object confValue = getConsumerConfValue("consumer.topicsPattern");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public int getConsumerTimeoutSeconds() {
        Object confValue = getConsumerConfValue("consumer.timeout");
        if (confValue == null)
            return -1; // infinite
        else
            return Integer.parseInt(confValue.toString());
    }
    public String getConsumerSubscriptionName() {
        Object confValue = getConsumerConfValue("consumer.subscriptionName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getConsumerSubscriptionType() {
        Object confValue = getConsumerConfValue("consumer.subscriptionType");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getConsumerName() {
        Object confValue = getConsumerConfValue("consumer.consumerName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }


    //////////////////
    // Get Pulsar reader related config
    public Map<String, Object> getReaderConfMap() {
        return this.readerConfMap;
    }
    public boolean hasReaderConfKey(String key) {
        if (key.contains(READER_CONF_PREFIX))
            return readerConfMap.containsKey(key.substring(READER_CONF_PREFIX.length() + 1));
        else
            return readerConfMap.containsKey(key);
    }
    public Object getReaderConfValue(String key) {
        if (key.contains(READER_CONF_PREFIX))
            return readerConfMap.get(key.substring(READER_CONF_PREFIX.length() + 1));
        else
            return readerConfMap.get(key);
    }
    public void setReaderConfValue(String key, Object value) {
        if (key.contains(READER_CONF_PREFIX))
            readerConfMap.put(key.substring(READER_CONF_PREFIX.length() + 1), value);
        else
            readerConfMap.put(key, value);
    }
    // Other consumer helper functions ...
    public String getReaderTopicName() {
        Object confValue = getReaderConfValue("reader.topicName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getReaderName() {
        Object confValue = getReaderConfValue("reader.readerName");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
    public String getStartMsgPosStr() {
        Object confValue = getReaderConfValue("reader.startMessagePos");
        if (confValue == null)
            return "";
        else
            return confValue.toString();
    }
}
