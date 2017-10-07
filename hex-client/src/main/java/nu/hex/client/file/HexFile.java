package nu.hex.client.file;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import nu.hex.client.Provider;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
public class HexFile {

    private String id;
    private String name;
    private String path;
    private String mediaType;
    private Provider storageProvider;
    private Boolean isFolder;
    private LocalDateTime timestamp;
    private File content;
    private String sourceData;

    public HexFile() {
    }

    public HexFile(String name) {
        this(null, name);
    }

    public HexFile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Provider getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(Provider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public Boolean isFolder() {
        return isFolder != null ? isFolder : Boolean.FALSE;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTimestampAsString() {
        return timestamp.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
    }

    public Optional<File> getContent() {
        return Optional.ofNullable(content);
    }

    public void setContent(File content) {
        this.content = content;
    }

    public String getResponse() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    public String toJsonString() {
        StringBuilder result = new StringBuilder("{");
        if (getId() != null) {
            result.append("\"id\":\"").append(getId()).append("\",");
        }
        if (isFolder()) {
            result.append("\"is-folder\":true,");
        }
        if (getPath() != null) {
            result.append("\"path\":\"").append(getPath()).append("\",");
        }
        if (getMediaType() != null) {
            result.append("\"media-type\":\"").append(getMediaType()).append("\",");
        }
        if (getStorageProvider() != null) {
            result.append("\"storage-provider\":\"").append(getStorageProvider()).append("\",");
        }
        if (getTimestamp() != null) {
            result.append("\"timestamp\":\"").append(getTimestampAsString()).append("\",");
        }
        if (getContent() != null && getContent().isPresent()) {
            result.append("\"file\":\"").append(getContent().get().getAbsolutePath()).append("\",");
        }
        if (getResponse() != null) {
            result.append("\"response\":").append(getResponse()).append(",");
        }
        String resultString = result.toString();
        resultString = resultString.substring(0, resultString.lastIndexOf(",")) + "}";
        return resultString;
    }
}
