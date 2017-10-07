package nu.hex.client.ext.google.drive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import java.util.Collections;
import java.util.Set;
import nu.hex.mediatype.CommonMediaType;
import nu.hex.mediatype.MediaTypeIdentifier;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
class MetaDataAssembler {

    public UploadFile createFile(java.io.File file, String parentIds) {
        File metaData = new File();
        metaData.setName(file.getName());
        metaData.setParents(Collections.singletonList(parentIds));
        String mediaType = CommonMediaType.APPLICATION_OCTET_STREAM;
        Set<String> mediaTypes = new MediaTypeIdentifier().getMediaTypeByFileSuffix(file.getAbsolutePath());
        if (!mediaTypes.isEmpty()) {
            mediaType = mediaTypes.iterator().next();
        }
        FileContent fileContent = new FileContent(mediaType, file);
        return new UploadFile(metaData, fileContent);
    }
}
