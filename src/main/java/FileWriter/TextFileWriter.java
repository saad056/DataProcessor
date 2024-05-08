package FileWriter;

import LogGenerator.Configuration.ConfigProperties;
import LogGenerator.LogWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Writes data to text file
 * @version 1.0
 * @author Agm Islam
 */

public class TextFileWriter implements FileWriterStrategy{

    private static LogWriter logWriter = new LogWriter();

    /**
     * Writes the given data to a text/csv file.
     *
     * This method writes the provided data to a text file, with each data entry
     * represented as a separate line in the file. It generates a unique file name
     * using the FileWriterFactory and logs information about the writing process
     * using a LogWriter.
     *
     * @param data List of the data to be written to the text file.
     * @throws IOException If an I/O error occurs while writing to the file.
     * @throws ClassNotFoundException If the class is not found
     */
    @Override
    public void writeToFile(List<Object> data) throws IOException, ClassNotFoundException {

        logWriter.writeInfoLog("-write file initiated headers " );
        String header = "data";
        String filename = FileWriterFactory.getFileName();

        FileWriter writer = null;

        // get the object of the class
        Class<?> clazz = Class.forName(ConfigProperties.classname);

        try {
            // Create the file if it doesn't exist
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();

            }

            // Create a FileWriter instance
            writer = new FileWriter(file,true);

            // Write headers if file is empty
            if (file.length() == 0) {
                writeHeaders(writer, clazz);
            }


            // Append data to the file
            for (Object obj : data) {
                writeData(writer, clazz, obj);
            }

            writer.flush();
        } catch (IOException e) {
            logWriter.writeInfoLog("Error writing in file " + e.getMessage());

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logWriter.writeInfoLog("Error closing FileWriter: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        logWriter.writeInfoLog("Data has been written successfully in " + filename);

    }

    /**
     * Writes the headers to the given FileWriter based on the public fields of the specified class.
     *
     * This method retrieves the public fields of the provided class using {@link Class#getFields()} method.
     * It then loops through these fields and writes their names as headers to the FileWriter, separated by commas.
     * After writing all headers, a newline character is appended to the FileWriter to move to the next line.
     *
     * @param writer The FileWriter instance to write headers to.
     * @param clazz The class whose public fields will be used as headers.
     * @throws IOException If an I/O error occurs while writing to the FileWriter.
     */
    private static void writeHeaders(FileWriter writer, Class<?> clazz) throws IOException {

        Field[] fields = clazz.getFields(); // get the public fields of the class

        // loop through the headers and writes in the file
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            writer.append(field.getName());

            // Append comma if not the last field
            if (i < fields.length - 1) {
                writer.append(",");
            }
        }
        writer.append("\n");


    }

    /**
     * Writes the data of the specified object to the given FileWriter based on the public fields of the specified class.
     *
     * This method retrieves the public fields of the provided class using {@link Class#getFields()} method.
     * It then loops through these fields, accesses their values from the provided object using reflection,
     * and appends the values to the FileWriter. Each value is separated by commas, and a newline character
     * is appended after writing all field values.
     *
     * @param writer The FileWriter instance to write data to.
     * @param clazz The class whose public fields will be used to access data from the object.
     * @param obj The object containing the data to be written.
     * @throws IOException If an I/O error occurs while writing to the FileWriter.
     */

    private static void writeData(FileWriter writer, Class<?> clazz, Object obj) throws IOException {
        Field[] fields = clazz.getFields(); // get the public fields of the class

        // loop through the fields and append data
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                writer.append(String.valueOf(value));

                // Append comma if not the last field
                if (i < fields.length - 1) {
                    writer.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        writer.append("\n");
    }
}
