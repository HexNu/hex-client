package nu.hex.client.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created 2016-nov-17
 *
 * @author hl
 */
public class SimpleFileWriter implements FileWriter {

    private final String doc;
    private final File file;

    public SimpleFileWriter(File file, String doc) {
        this.doc = doc;
        this.file = file;
    }

    @Override
    public File write() throws IOException {
        Files.write(file.toPath(), doc.getBytes());
        return file;
    }
}
