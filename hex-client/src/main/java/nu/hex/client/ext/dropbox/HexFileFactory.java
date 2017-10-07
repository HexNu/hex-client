package nu.hex.client.ext.dropbox;

import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.Provider;
import nu.hex.client.file.HexFile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
class HexFileFactory {

    private static final JSONParser parser = new JSONParser();

    public static HexFile createFromFolder(Metadata input) {
        HexFile result = new HexFile(input.getName());
        result.setPath(input.getPathDisplay());
        result.setStorageProvider(Provider.DROPBOX);
        result.setSourceData(input.toString());
        result.setIsFolder(Boolean.TRUE);
        try {
            JSONObject jo = (JSONObject) parser.parse(input.toString());
            String id = jo.get("id").toString();
            result.setId(id.substring(id.indexOf(":") + 1));
        } catch (ParseException ex) {
            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static HexFile createFromFolder(CreateFolderResult input) {
        return createFromFolder(input.getMetadata());
    }

    public static HexFile createFromFile(Metadata input) {
        HexFile result = new HexFile(input.getName());
        result.setPath(input.getPathDisplay());
        result.setStorageProvider(Provider.DROPBOX);
        result.setSourceData(input.toString());
        result.setIsFolder(Boolean.FALSE);
        try {
            JSONObject jo = (JSONObject) parser.parse(input.toString());
            result.setId(jo.get("id").toString());
        } catch (ParseException ex) {
            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static HexFile createFromFile(Metadata input, File file) {
        HexFile result = createFromFile(input);
        result.setStorageProvider(Provider.DROPBOX);
        result.setContent(file);
        result.setSourceData(input.toString());
        result.setIsFolder(Boolean.FALSE);
        try {
            JSONObject jo = (JSONObject) parser.parse(input.toString());
            result.setId(jo.get("id").toString());
        } catch (ParseException ex) {
            Logger.getLogger(HexFileFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
