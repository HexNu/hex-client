package nu.hex.client.ext.google.drive;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
public class GoogleDriveConfigBuilder {

    private final GoogleDriveConfig result;

    public GoogleDriveConfigBuilder(String clientIdentifier, String appRoot) {
        result = new GoogleDriveConfig(clientIdentifier, appRoot);
    }

    public GoogleDriveConfigBuilder appendFolderColorRGB(String folderColorRGB) {
        result.setFolderColorRGB(folderColorRGB);
        return this;
    }

    public GoogleDriveConfigBuilder appendScope(String key, String value) {
        result.addScope(key, value);
        return this;
    }

    public GoogleDriveConfig build() {
        result.save();
        return result;
    }
}
