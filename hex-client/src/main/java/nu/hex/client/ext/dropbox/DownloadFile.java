package nu.hex.client.ext.dropbox;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import nu.hex.client.exception.HexClientException;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class DownloadFile {

    private final OutputStream stream;
    private final String path;
    private final File tempFile;
    private final String resultFileName;

    public DownloadFile(OutputStream stream, String path, File tempFile, String resultFile) {
        this.stream = stream;
        this.path = path;
        this.tempFile = tempFile;
        this.resultFileName = resultFile;
    }

    public OutputStream getStream() {
        return stream;
    }

    public String getPath() {
        return path;
    }

    public File getTempFile() {
        return tempFile;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void closeStream() {
        try {
            stream.close();
        } catch (IOException ex) {
            throw new HexClientException("Could not close file output stream", ex);
        }
    }

}
