package nu.hex.client.ext.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.exception.HexClientException;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class MetaDataAssembler {

    private final DropboxConfig dropboxConfig;

    public MetaDataAssembler(DropboxConfig dropboxConfig) {
        this.dropboxConfig = dropboxConfig;
    }

    UploadFile createUploadFile(String path, File file) throws HexClientException {
        try {
            InputStream in = new FileInputStream(file);
            String filePath = path + "/" + file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
            return new UploadFile(in, filePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MetaDataAssembler.class.getName()).log(Level.SEVERE, "Could not read file " + file.getAbsolutePath(), ex);
            throw new HexClientException("Could not read file for upload", ex);
        }
    }

    DownloadFile createDownloadFile(String path) throws HexClientException {
        try {
            String prefix = path.substring(path.lastIndexOf("/"), path.lastIndexOf("."));
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            String directory = dropboxConfig.getAppRootFolder() + "/.tmp";
            File tmpDir = new File(directory);
            File tmpFile = File.createTempFile(prefix, "." + suffix, tmpDir);
            OutputStream out = new FileOutputStream(tmpFile);
            String cleanedUpName = prefix + "." + suffix;
            return new DownloadFile(out, path, tmpFile, cleanedUpName);
        } catch (IOException ex) {
            throw new HexClientException("Could not store file " + path.substring(path.lastIndexOf("/") + 1) + " locally", ex);
        }
    }

}
