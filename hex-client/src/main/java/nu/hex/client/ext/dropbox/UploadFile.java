package nu.hex.client.ext.dropbox;

import java.io.IOException;
import java.io.InputStream;
import nu.hex.client.exception.HexClientException;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class UploadFile {

    private final InputStream stream;
    private final String path;

    public UploadFile(InputStream stream, String path) {
        this.stream = stream;
        this.path = path;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getPath() {
        return path;
    }

    public void closeStream() {
        try {
            stream.close();
        } catch (IOException ex) {
            throw new HexClientException("Could not close file input stream", ex);
        }
    }
}
