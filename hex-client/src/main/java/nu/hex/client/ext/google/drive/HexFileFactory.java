package nu.hex.client.ext.google.drive;

import com.dropbox.core.v2.files.CreateFolderResult;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.Provider;
import nu.hex.client.file.HexFile;
import nu.hex.mediatype.MediaTypeIdentifier;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
public class HexFileFactory {

    private static final MediaTypeIdentifier IDENTIFIER = new MediaTypeIdentifier();

    public static HexFile createFromFolder(File input) {
        HexFile result = new HexFile(input.getName());
        result.setId(input.getId());
        result.setMediaType(input.getMimeType());
        result.setIsFolder(Boolean.TRUE);
        result.setSourceData(input.toString());
        try {
            System.out.println(input.toPrettyString());
//        result.setPath(input.get);
//        try {
////            JSONObject jo = (JSONObject) parser.parse(input.toString());
////            result.setIsFolder(getAsBoolean(jo, ".tag", "folder"));
////        } catch (ParseException ex) {
////            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        } catch (IOException ex) {
            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static HexFile createFromFolder(CreateFolderResult input) {
//        return createFromFolder(input.getMetadata());
        return null;
    }

    public static HexFile createFromFile(File input) {
        HexFile result = new HexFile(input.getId(), input.getName());
        result.setMediaType(input.getMimeType());
        result.setId(input.getId());
        result.setSourceData(input.toString());
        result.setStorageProvider(Provider.GOOGLE_DRIVE);
//        result.setPath(input.getPathDisplay());
//        try {
//            JSONObject jo = (JSONObject) parser.parse(input.toString());
//            result.setIsFolder(false);
//        } catch (ParseException ex) {
//            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return result;
    }

    public static HexFile createFromFile(File input, java.io.File file) {
        HexFile result = createFromFile(input);
        result.setId(input.getId());
        Set<String> mediaTypeByFileSuffix = IDENTIFIER.getMediaTypeByFileSuffix(file.getAbsolutePath());
        if (mediaTypeByFileSuffix.isEmpty()) {
            result.setMediaType(input.getMimeType());
        } else {
            result.setMediaType(mediaTypeByFileSuffix.iterator().next());
        }
        result.setSourceData(input.toString());
        result.setContent(file);
        return result;
    }

//    private static Boolean getAsBoolean(JSONObject obj, String key, String expectedValue) {
//        return ((String) obj.get(key)).equals(expectedValue);
//    }
}
