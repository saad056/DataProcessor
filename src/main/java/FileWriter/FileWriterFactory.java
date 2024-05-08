package FileWriter;

import LogGenerator.Configuration.ConfigProperties;

import java.util.UUID;


/**
 * Gets the appropriate data format writer object
 * @version 1.0
 * @author Agm Islam
 */
public class FileWriterFactory {

    /**
     * Returns a FileWriterStrategy instance based on the specified file type.
     *
     * This method returns an instance of FileWriterStrategy corresponding to the
     * specified file type. Supported file types include "text" and "csv".
     *
     * @param fileType The type of file to write.
     * @return A FileWriterStrategy instance for the specified file type.
     * @throws IllegalArgumentException If the specified file type is not supported.
     */
    public static FileWriterStrategy getWriter(String fileType) {
        switch (fileType) {
            case "text":
            case "csv":
                return new TextFileWriter();
            default:
                throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    /**
     * Generates a unique file name.
     *
     * This method generates a unique file name using a UUID and the configured
     * data file path and file extension from the configuration properties.
     *
     * @return A unique file name.
     */
    public static String getFileName(){
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = ConfigProperties.dataFilePath + "_" + uuid + "." + ConfigProperties.fileExtension;
        return uniqueFileName;
    }
}
