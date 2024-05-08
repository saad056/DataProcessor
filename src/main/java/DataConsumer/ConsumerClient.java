package DataConsumer;

import LogGenerator.Configuration.ConfigProperties;
import FileWriter.FileWriterFactory;
import FileWriter.FileWriterStrategy;
import LogGenerator.LogWriter;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Data Consumer: consumes data from the kafka topic and writes in the defined format
 * @version 1.0
 * @author Agm Islam
 */
public class ConsumerClient implements Runnable {

    /**
     * Logger
     */
    private static final LogWriter logWriter = new LogWriter();

    /**
     * Creates a lock
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Kafka Message Container for thread safe
     */
    private static final List<Object> messageList = Collections.synchronizedList(new ArrayList<>());


    /**
     * Defines the Kafka Consumer properties.
     *
     * Subscribing to kafka topic.
     *
     * Consumes max number of records at once
     *
     * After the message list exceeds a threshold, message list is cleared and write to file
     * the main logic that the thread will execute when started. It should be overridden
     * with the specific behavior that needs to be performed by the thread.
     *
     * @see Runnable
     */
    public void run() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigProperties.kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ConfigProperties.kafkaGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, ConfigProperties.kafkaOffset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, ConfigProperties.kafkaMaxRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        try {
            logWriter.writeInfoLog("Thread Started");


            consumer.subscribe(Arrays.asList(ConfigProperties.kafkaTopic));
            ObjectMapper objectMapper = new ObjectMapper();
            String classname = ConfigProperties.classname;

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));


                for (ConsumerRecord<String, String> record : records) {
                    try {

                        Class<?> clazz = Class.forName(classname);                  // Dynamically load the class
                        Object obj = objectMapper.readValue(record.value(), clazz); // Deserialize JSON string to the loaded class
                        messageList.add(obj);                                       // Add to the list


                    } catch (ClassNotFoundException e) {
                        logWriter.writeErrorLog("Error reading data of " + classname + ": " + e.getMessage());

                    }
                }

                // if the list size exceeds threshold, writes to the file and empty the list
                if(messageList.size()>=ConfigProperties.dataMaxRecord){

                    lock.lock();                                                // acquring lock
                    List<Object> messagesCopy = new ArrayList<>(messageList);   // copy the list to be written
                    writeMessagesToFile(messagesCopy);                          // writes to file
                    messageList.removeAll(messagesCopy);                        //remove from the list
                    lock.unlock();                                              // release the lock


                }

            }
        } catch (Exception e) {
            logWriter.writeErrorLog(e.getMessage());
        } finally {
            logWriter.writeErrorLog("Consumer is shutting down");
            consumer.close();
        }
    }

    /**
     * Writes the given list of messages to a file using the appropriate file writer strategy.
     *
     * This method synchronizes access to ensure thread safety when writing to the file.
     * It checks if the list of messages is not empty, retrieves the file type from the
     * configuration properties, and obtains the corresponding file writer strategy using
     * the FileWriterFactory. Then, it attempts to write the messages to the file using
     * the selected file writer strategy. If an IOException occurs during the writing
     * process, it logs an error message using the log writer.
     *
     * @param msg A list of messages to be written to the file.
     */
    private synchronized static void writeMessagesToFile(List<Object> msg) {

        if (!msg.isEmpty()) {
            String fileType = ConfigProperties.dataType;
            FileWriterStrategy fileWriter = FileWriterFactory.getWriter(fileType);
            try {
                fileWriter.writeToFile(msg);

            } catch (Exception e) {
                logWriter.writeErrorLog("Error writing data in the file: " + fileType);
            }

        }
    }

    /**
     * Entry point of the application.
     *
     * This method initializes a ConsumerClient and creates a fixed thread pool executor
     * based on the number of threads specified in the configuration properties.
     * It then submits instances of the ConsumerClient to the executor for execution.
     * After all tasks are submitted, it shuts down the executor to release its resources.
     *
     * @param args The command-line arguments passed to the program (not used).
     */
    public static void main(String[] args) {
        ConsumerClient consumerClient = new ConsumerClient();
        int numofThreads = ConfigProperties.noOfThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numofThreads);

        for (int i = 0; i < numofThreads; i++) {
            executor.submit(consumerClient);
        }

        // Shutdown the executor once the thread stops
        executor.shutdown();
    }
}
