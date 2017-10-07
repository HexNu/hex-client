package nu.hex.client;

import nu.hex.client.ext.dropbox.DropboxConfig;
import nu.hex.client.ext.dropbox.DropboxFileClient;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class Main {

    public static void main(String[] args) {
        DropboxConfig dropboxConfig = new DropboxConfig("HexRpgManager", "hrm-files");
//        Auth auth = new Auth(dropboxConfig);
//        auth.start();
//        auth.finish("TnWGat5LB5QAAAAAAAAudyJen7e8kgzPUdqDzmzYlYw");
        DropboxFileClient client = new DropboxFileClient(dropboxConfig);
        client.getFiles("/hrm-files/backup").forEach(System.out::println);
    }

}
