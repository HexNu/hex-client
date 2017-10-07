package nu.hex.client.ext;

import java.io.File;
import java.util.List;
import java.util.Optional;
import nu.hex.client.Client;
import nu.hex.client.exception.HexClientException;
import nu.hex.client.file.HexFile;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
public interface FileClient extends Client {

    Optional<HexFile> createFolder(String path, String name) throws HexClientException;

    Optional<HexFile> uploadFile(String path, File file) throws HexClientException;

    List<HexFile> getFiles(String path) throws HexClientException;

    Optional<HexFile> getFolder(String path) throws HexClientException;

    Optional<HexFile> getFileMetadata(String path) throws HexClientException;

    Optional<HexFile> downloadFile(String path, File targetFolder) throws HexClientException;
}
