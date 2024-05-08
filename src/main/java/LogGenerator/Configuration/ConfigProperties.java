package LogGenerator.Configuration;

import org.apache.kafka.common.protocol.types.Field;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration from the properties file
 * @version 1.0
 * @author Agm Islam
 */

public class ConfigProperties {
    /**
     * Properties Object
     */
    private static final Properties properties = new Properties();

    /**
     * COnfiguration file path
     */
    private static final String resourcepath = "src/main/resources/application.properties";

    /**
     * Load configuration from the configuration file
     */
    static {
        try (InputStream input = new FileInputStream(resourcepath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Kafka Server
     */
    public static final String kafkaServer = properties.getProperty("kafka.bootstrap.servers");

    /**
     * Kafka group Id
     */
    public static final String kafkaGroupId = properties.getProperty("kafka.group.id");

    /**
     * Kafka Topic
     */
    public static final String kafkaTopic = properties.getProperty("kafka.topic.name");

    /**
     * kafka topic, where the consumer starts listening from
     */
    public static final String kafkaOffset = properties.getProperty("kafka.topic.offset");

    /**
     * Maximum number of records to poll
     */
    public static final int kafkaMaxRecords = Integer.parseInt(properties.getProperty("kafka.records.maxnumrecords"));


    /**
     * log file location
     */
    public static final String logFilePath = properties.getProperty("logfilePath");

    /**
     * max file size of the log
     */
    public static final int maxLogFileSize = Integer.parseInt(properties.getProperty("maxfilesize"));

    /**
     * File name pattern
     */
    public static final String logFilePattern = properties.getProperty("logfilepattern");

    /**
     * Number of Threads
     */
    public static final int noOfThreads = Integer.parseInt(properties.getProperty("noThreads"));

    /**
     * Number of data to write at once
     */
    public static final int dataMaxRecord = Integer.parseInt(properties.getProperty("numberofdatarecords"));

    /**
     * Idle time to wait to write data
     */
    public static final int idleWaitTime = Integer.parseInt(properties.getProperty("maxidletime"));

    /**
     * File path where data will be written
     */
    public static final String dataFilePath = (String) properties.get("datafilepath");

    /**
     * Data Type
     */
    public static final String dataType = properties.getProperty("dataType");

    /**
     * Data Type Extension
     */
    public static final String fileExtension = properties.getProperty("fileextesion");

    /**
     * Class name to be consumed from kafka topic
     */
    public static final String classname = properties.getProperty("classname");
}