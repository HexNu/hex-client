package nu.hex.client.ext.google.drive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class UploadFile {

    private final File metaData;
    private final FileContent content;

    public UploadFile(File metaData, FileContent content) {
        this.metaData = metaData;
        this.content = content;
    }

    public File getMetaData() {
        return metaData;
    }

    public FileContent getContent() {
        return content;
    }
}
