package LogGenerator;

import LogGenerator.Configuration.ConfigProperties;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

/**
 * Log Writer
 * Log writer will generate a new file after the threshold size exceeds.
 * @version 1.0
 * @author Agm Islam

 */

public class LogWriter {

    private static LoggerContext loggerContext;
    private static Logger logger;

    /**
     * Constructor
     */
    public LogWriter(){


        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger(LogWriter.class);

        // Configure rolling file appender
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setContext(loggerContext);
        fileAppender.setFile(ConfigProperties.logFilePath);

        // Configure rolling policy
        SizeAndTimeBasedRollingPolicy rollingPolicy = new SizeAndTimeBasedRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(ConfigProperties.logFilePattern);
        rollingPolicy.setMaxFileSize(FileSize.valueOf(String.valueOf(ConfigProperties.maxLogFileSize)));
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.start();

        // Configure encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%date [%thread] %-5level %logger{35} - %msg%n");
        encoder.start();

        // Attach rolling policy and encoder to file appender
        fileAppender.setEncoder(encoder);
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        // add appender to logger
        logger.addAppender(fileAppender);
    }

    /**
     * Writes Info Message
     * @param msg Info Message
     */
    public static void writeInfoLog(String msg){
        logger.setLevel(Level.INFO);
        logger.info(msg);

    }

    /***
     * Writes Error Log
     * @param msg Error Message
     */
    public static void writeErrorLog(String msg){
        logger.setLevel(Level.ERROR);
        logger.error(msg);

    }

    /**
     * Writes a log message with the specified level.
     *
     * @param msg   The message to be logged.
     * @param level The level at which the message should be logged.
     *              Supported levels are:
     *              <ul>
     *                <li>{@link ch.qos.logback.classic.Level#TRACE}</li>
     *                <li>{@link ch.qos.logback.classic.Level#DEBUG}</li>
     *                <li>{@link ch.qos.logback.classic.Level#INFO}</li>
     *                <li>{@link ch.qos.logback.classic.Level#WARN}</li>
     *                <li>{@link ch.qos.logback.classic.Level#ERROR}</li>
     *              </ul>
     */
    public static void writeLog(String msg, Level level) {
        switch (level.levelInt) {
            case Level.TRACE_INT:
                logger.trace(msg);
                break;
            case Level.DEBUG_INT:
                logger.debug(msg);
                break;
            case Level.INFO_INT:
                logger.info(msg);
                break;
            case Level.WARN_INT:
                logger.warn(msg);
                break;
            case Level.ERROR_INT:
                logger.error(msg);
                break;
            default:
                logger.info(msg);
                break;
        }
    }
}
