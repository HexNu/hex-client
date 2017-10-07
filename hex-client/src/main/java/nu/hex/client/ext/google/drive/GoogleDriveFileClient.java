package nu.hex.client.ext.google.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nu.hex.client.Provider;
import nu.hex.client.exception.HexClientException;
import nu.hex.client.ext.FileClient;
import nu.hex.client.file.HexFile;
import nu.hex.mediatype.CommonMediaType;

/**
 * Created 2017-okt-04
 *
 * @author hl
 */
public class GoogleDriveFileClient implements FileClient {

    private static final String FILE_FIELDS = "files(id, name, mimeType, parents, folderColorRgb)";
//    private static final String FOLDER_COLOR_RGB = "#fbe983";
    private final Drive service;
    private final GoogleDriveConfig config;

    public GoogleDriveFileClient(GoogleDriveConfig config) {
        this.config = config;
        this.service = new Auth(config).getDriveService();
        String projectFolderId = getProjectFolderId();
        System.out.println(projectFolderId);
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE_DRIVE;
    }

    /**
     *
     * @param path
     * @param file java.io.File
     * @return
     * @throws HexClientException
     */
    @Override
    public Optional<HexFile> uploadFile(String path, java.io.File file) throws HexClientException {
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        Optional<File> optParent = getFolderMetadata(path);
        if (!optParent.isPresent()) {
            return Optional.empty();
        }
        File parent = optParent.get();
        try {
            UploadFile uploadFile = new MetaDataAssembler().createFile(file, parent.getId());
            service.files().create(uploadFile.getMetaData(), uploadFile.getContent())
                    .setFields("id")
                    .execute();
            return getFileMetadata(path + "/" + file.getName());
        } catch (IOException ex) {
            throw new HexClientException("Could not create file", ex);
        }
    }

    @Override
    public Optional<HexFile> createFolder(String path, String name) throws HexClientException {
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        getFolderMetadata(path).ifPresent((parent) -> {
            addFolder(name, parent.getId());
        });
        Optional<File> optFolder = getFolderMetadata(path + "/" + name);
        if (optFolder.isPresent()) {
            return Optional.of(HexFileFactory.createFromFolder(optFolder.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<HexFile> getFiles(String path) throws HexClientException {
        List<HexFile> result = new ArrayList<>();
        getFolderMetadata(path).ifPresent((folder) -> {
            getAllFiles(folder.getId()).forEach((file) -> {
                result.add(HexFileFactory.createFromFile(file));
            });
        });
        return result;
    }

    @Override
    public Optional<HexFile> getFileMetadata(String path) throws HexClientException {
        path = path.startsWith("/") ? path : "/" + path;
        String folderPath = path.substring(0, path.lastIndexOf("/"));
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        for (HexFile f : getFiles(folderPath)) {
            if (f.getName().equals(fileName)) {
                return Optional.of(f);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<HexFile> downloadFile(String path, java.io.File targetFolder) throws HexClientException {
        Optional<HexFile> optMetadata = getFileMetadata(path);
        if (optMetadata.isPresent()) {
            try {
                java.io.File targetFile = new java.io.File(targetFolder.getAbsolutePath() + path.substring(path.lastIndexOf("/")));
                OutputStream target = new FileOutputStream(targetFile);
                HexFile hexFile = optMetadata.get();
                service.files().get(hexFile.getId()).executeMediaAndDownloadTo(target);
                hexFile.setContent(targetFile);
                return Optional.of(hexFile);
            } catch (IOException ex) {
                throw new HexClientException("Could not download file " + path, ex);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<HexFile> getFolder(String path) {
        Optional<File> optFolder = getFolderMetadata(path);
        if (optFolder.isPresent()) {
            HexFile result = HexFileFactory.createFromFolder(optFolder.get());
            result.setPath(path);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private Optional<File> getFolderMetadata(String path) throws HexClientException {
        return Optional.ofNullable(walk(path));
    }

    private String getProjectFolderId() {
        Optional<String> rootId = config.getRootId();
        if (rootId.isPresent()) {
            return rootId.get();
        } else {
            try {
                File fileMetadata = new File();
                config.getRootFolder().ifPresent(fileMetadata::setName);
                config.getFolderColorRGB().ifPresent(fileMetadata::setFolderColorRgb);
                fileMetadata.setMimeType(CommonMediaType.APPLICATION_VND_GOOGLE_APPS_FOLDER);
                File file = service.files().create(fileMetadata)
                        .setFields("id")
                        .execute();
                config.setRootFolderId(file.getId());
                config.save();
                return file.getId();
            } catch (IOException ex) {
                throw new HexClientException("Could not create project folder", ex);
            }
        }
    }

    private File walk(String path) throws HexClientException {
        return walk(path, getProjectFolderId());
    }

    private File walk(String path, String parentId) throws HexClientException {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        File result = null;
        if (path.contains("/")) {
            String secondPart = path.substring(path.indexOf("/") + 1);
            if (secondPart.endsWith("/")) {
                secondPart = secondPart.substring(0, secondPart.length() - 1);
            }
            if (secondPart.contains("/")) {
                secondPart = secondPart.substring(0, secondPart.indexOf("/"));
            }
            for (File f : getAllFiles(parentId)) {
                if (f.getName().equalsIgnoreCase(secondPart)) {
                    if (f.getName().equalsIgnoreCase(secondPart)) {
                        String newPath = path.substring(path.indexOf("/") + 1);
                        File next = walk(newPath, f.getId());
                        result = next == null ? f : next;
                    }
                }
            }
        } else {
            try {
                result = service.files().get(parentId).execute();
            } catch (IOException ex) {
                throw new HexClientException("Could not download folder with id " + parentId, ex);
            }
        }
        return result;
    }

    private List<File> getAllFiles(String parentId) throws HexClientException {
        try {
            FileList fileList = service.files().list().setPageSize(100)
                    .setFields("nextPageToken, " + FILE_FIELDS)
                    .setQ("'" + parentId + "' in parents")
                    .execute();
            return fileList.getFiles();
        } catch (IOException ex) {
            throw new HexClientException("Could not list files", ex);
        }
    }

    private Optional<String> addFolder(String folderName, String parentId) throws HexClientException {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            config.getFolderColorRGB().ifPresent(fileMetadata::setFolderColorRgb);
            if (parentId == null) {
                fileMetadata.setParents(Collections.singletonList(getProjectFolderId()));
            } else {
                fileMetadata.setParents(Collections.singletonList(parentId));
            }
            fileMetadata.setMimeType(CommonMediaType.APPLICATION_VND_GOOGLE_APPS_FOLDER);
            File file = service.files().create(fileMetadata).setFields("id").execute();
            return Optional.ofNullable(file.getId());
        } catch (IOException ex) {
            throw new HexClientException("Could not create project folder", ex);
        }
    }
}
