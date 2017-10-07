package nu.hex.client.ext.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nu.hex.client.Provider;
import nu.hex.client.exception.HexClientException;
import nu.hex.client.ext.FileClient;
import nu.hex.client.file.HexFile;
import nu.hex.client.properties.HexClientProperties;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
public class DropboxFileClient implements FileClient {

    private final DropboxConfig dropboxConfig;
    private final DbxRequestConfig config;
    private final DbxClientV2 client;

    public DropboxFileClient(DropboxConfig dropboxConfig) {
        this.dropboxConfig = dropboxConfig;
        config = new DbxRequestConfig(dropboxConfig.getClientIdentifier());
        client = new DbxClientV2(config, HexClientProperties.getInstance().getProperty(Auth.DBX_ACCESS_TOKEN));
    }

    @Override
    public Provider getProvider() {
        return Provider.DROPBOX;
    }

    @Override
    public Optional<HexFile> createFolder(String path, String name) throws HexClientException {
        path = path == null ? "" : path;
        return createFolder(path + "/" + name);
    }

    private Optional<HexFile> createFolder(String folder) throws HexClientException {
        folder = cleanUpPath(folder);
        try {
            CreateFolderResult result = client.files().createFolderV2(folder);
            return Optional.of(HexFileFactory.createFromFolder(result));
        } catch (DbxException ex) {
            throw new HexClientException("Folder " + folder + " could not be created.", ex);
        }
    }

    @Override
    public Optional<HexFile> uploadFile(String path, File file) throws HexClientException {
        path = cleanUpPath(path);
        UploadFile uploadFile = new MetaDataAssembler(dropboxConfig).createUploadFile(path, file);
        try {
            FileMetadata response = client.files()
                    .uploadBuilder(uploadFile.getPath())
                    .uploadAndFinish(uploadFile.getStream());
            return Optional.of(HexFileFactory.createFromFile(response));
        } catch (DbxException | IOException ex) {
            throw new HexClientException("Could not upload file", ex);
        } finally {
            uploadFile.closeStream();
        }
    }

    @Override
    public List<HexFile> getFiles(String path) throws HexClientException {
        path = cleanUpPath(path);
        List<HexFile> result = new ArrayList<>();
        try {
            ListFolderResult listFolder = client.files().listFolder(path);
            listFolder.getEntries().forEach((entry) -> {
                result.add(HexFileFactory.createFromFile(entry));
            });
        } catch (DbxException ex) {
            throw new HexClientException("Could not read folder content", ex);
        }
        return result;
    }

    @Override
    public Optional<HexFile> getFolder(String path) throws HexClientException {
        path = cleanUpPath(path);
        try {
            Metadata result = client.files().getMetadata(path);
            if (result != null) {
                return Optional.of(HexFileFactory.createFromFolder(result));
            }
        } catch (DbxException ex) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<HexFile> getFileMetadata(String path) throws HexClientException {
        try {
            path = cleanUpPath(path);
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            path = path.substring(0, path.lastIndexOf("/"));
            ListFolderResult listFolder = client.files().listFolder(path);
            for (Metadata fm : listFolder.getEntries()) {
                if (fm.getName().equals(fileName)) {
                    return Optional.of(HexFileFactory.createFromFile(fm));
                }
            }
        } catch (DbxException ex) {
            throw new HexClientException("Could not read parent folder", ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<HexFile> downloadFile(String path, File targetFolder) throws HexClientException {
        if (!path.contains(".")) {
            throw new HexClientException("Path must end with a file name including a suffix", new IllegalArgumentException("Invalid filename: Missing suffix"));
        }
        path = cleanUpPath(path);
        DownloadFile downloadFile = new MetaDataAssembler(dropboxConfig).createDownloadFile(path);
        File result = new File(targetFolder.getAbsolutePath() + downloadFile.getResultFileName());
        try {
            FileMetadata download = client.files().downloadBuilder(downloadFile.getPath()).download(downloadFile.getStream());
            Files.copy(downloadFile.getTempFile().toPath(), new FileOutputStream(result));
            return Optional.of(HexFileFactory.createFromFile(download, result));
        } catch (DbxException | IOException ex) {
            throw new HexClientException("Could not download file", ex);
        } finally {
            downloadFile.closeStream();
            downloadFile.getTempFile().delete();
        }
    }

    private String cleanUpPath(String path) {
        if (path.startsWith(dropboxConfig.getAppRootFolder())) {
            path = path.substring(dropboxConfig.getAppRootFolder().length());
        }
        return path;
    }

}
