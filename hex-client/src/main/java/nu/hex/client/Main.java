package nu.hex.client;

import com.google.api.services.drive.DriveScopes;
import java.util.List;
import nu.hex.client.ext.google.drive.GoogleDriveFileClient;
import nu.hex.client.ext.google.drive.GoogleDriveConfig;
import nu.hex.client.ext.google.drive.GoogleDriveConfigBuilder;
import nu.hex.client.file.HexFile;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class Main {
////
//    public static void main(String[] args) {
//        GoogleDriveConfig config = new GoogleDriveConfigBuilder("HexRpgManager", "hrm-files")
//                .appendFolderColorRGB("#fbe983")
//                .appendScope("DRIVE_METADATA", DriveScopes.DRIVE_METADATA)
//                .appendScope("DRIVE_FILE", DriveScopes.DRIVE_FILE)
//                .build();
//        GoogleDriveFileClient client = new GoogleDriveFileClient(config);
//        client.createFolder("/hrm-files", "new folder").ifPresent(System.out::println);
////        client.createFolder("/hrm-files", "backup");
////        client.getFolder("/hrm-files/backup").ifPresent(System.out::println);
////        client.getFiles("/hrm-files/backup").forEach(System.out::println);
//////        DropboxConfig dropboxConfig = new DropboxConfig("HexRpgManager", "hrm-files");
////////        Auth auth = new Auth(dropboxConfig);
////////        auth.start();
////////        auth.finish("TnWGat5LB5QAAAAAAAAudyJen7e8kgzPUdqDzmzYlYw");
//////        DropboxFileClient client = new DropboxFileClient(dropboxConfig);
//////        client.getFiles("/hrm-files/backup").forEach(System.out::println);
////
////        GoogleDriveConfig googleDriveConfig = new GoogleDriveConfig("HexRpgManager", "hrm-files");
////        googleDriveConfig.addScope("DRIVE_METADATA", DriveScopes.DRIVE_METADATA);
////        googleDriveConfig.addScope("DRIVE_FILE", DriveScopes.DRIVE_FILE);
////        googleDriveConfig.setFolderColorRGB("#fbe983");
////        googleDriveConfig.save();
////        GoogleDriveFileClient client = new GoogleDriveFileClient(googleDriveConfig);
////        client.createFolder("/hrm-files/backup", "slask1").ifPresent(System.out::println);
////        List<HexFile> files = client.getFiles("/hrm-files/backup");
////        files.forEach(System.out::println);
//    }
//
}
