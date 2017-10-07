package nu.hex.client.ext.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.exception.HexClientException;

/**
 * Created 2017-okt-04
 *
 * @author hl
 */
public class Auth {

//    private static final String APPLICATION_NAME = "Hex Rpg Manager";
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials");
    private FileDataStoreFactory dataStoreFactory;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;
    private final List<String> scopes = new ArrayList<>();
    private java.io.File dataStore;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    private final GoogleDriveConfig config;

    public Auth(GoogleDriveConfig config) {
        this.config = config;
        config.getClientIdentifier().ifPresent((id) -> {
            createDataStoreFactory(id);
        });
        config.getScopes().forEach(scopes::add);
    }

    private void createDataStoreFactory(String id) {
        dataStore = new java.io.File(DATA_STORE_DIR + "/" + id);
        dataStore.mkdirs();
        try {
            dataStoreFactory = new FileDataStoreFactory(dataStore);
        } catch (IOException ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Credential authorize() throws HexClientException {
        try {
            InputStream in = Auth.class.getResourceAsStream("/client_secret.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            Credential credential = buildUserAuthorizationRequest(clientSecrets);
            return credential;
        } catch (IOException ex) {
            throw new HexClientException("Could not get Credentials from Google Drive", ex);
        }
    }

    private Credential buildUserAuthorizationRequest(GoogleClientSecrets clientSecrets) throws HexClientException {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow(clientSecrets);
            Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
            Logger.getLogger(Auth.class.getName()).log(Level.INFO, "Credentials saved to {0}", dataStore.getAbsolutePath());
            return credential;
        } catch (IOException ex) {
            throw new HexClientException("Could not build Google Drive authorization request", ex);
        }
    }

    private GoogleAuthorizationCodeFlow buildFlow(GoogleClientSecrets clientSecrets) throws HexClientException {
        try {
            return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();
        } catch (IOException ex) {
            throw new HexClientException("Could not build authorization code flow", ex);
        }
    }

    public Drive getDriveService() throws HexClientException {
        Credential credential = authorize();
        Optional<String> clientIdentifier = config.getClientIdentifier();
        if (clientIdentifier.isPresent()) {
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(clientIdentifier.get())
                    .build();
        }
        throw new HexClientException("Could not create Drive Service");
    }
}
