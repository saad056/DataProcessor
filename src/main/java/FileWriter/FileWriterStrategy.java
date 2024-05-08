package FileWriter;

import java.io.IOException;
import java.util.List;

/**
 * Filewriter Interface
 * @version 1.0
 * @author Agm Islam
 */
public interface FileWriterStrategy {

    /**
     * Writes the given data to a file.
     *
     * Implementations of this method should define the specific strategy for writing
     * the data to a file.
     *
     * @param data The data to be written to the file.
     * @throws IOException If an I/O error occurs while writing to the file.
     * @throws ClassNotFoundException If Class is not found
     */
    void writeToFile(List<Object> data) throws IOException, ClassNotFoundException;
}
