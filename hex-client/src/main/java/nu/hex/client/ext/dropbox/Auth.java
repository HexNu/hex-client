package nu.hex.client.ext.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.exception.HexClientException;
import nu.hex.client.properties.HexClientProperties;

/**
 * Created 2017-okt-07
 *
 * @author hl
 * <p>
 * Auth is used to ask user for permission to store files in the users Dropbox
 * Account.
 * <p>
 * start() will (hopefully) take the user to the Authorization page on Dropbox.
 * Ass a fallback it returns the url to that same page.
 * <p>
 * finish(String accesToken) is used to store the AccessToken in the users file
 * system.
 *
 *
 */
public class Auth {

    public static final String DBX_ACCESS_TOKEN = "dbx-access-token";
    private final DbxAppInfo appInfo;
    private final DbxRequestConfig config;
    private final DbxWebAuth webAuth;
    private DbxAuthFinish authFinish;

    /**
     * Authorize the application on Dropbox.
     * <p>
     * Auth is used to ask user for permission to store files in the users
     * Dropbox
     *
     * @param dropboxConfig The DropboxConfig for the application.
     */
    public Auth(DropboxConfig dropboxConfig) {
        appInfo = new DbxAppInfo(dropboxConfig.getAppKey(), dropboxConfig.getSecret());
        config = new DbxRequestConfig(dropboxConfig.getClientIdentifier());
        webAuth = new DbxWebAuth(config, appInfo);
    }

    /**
     * Begin authorization.
     * <p>
     * This method will (hopefully) take the user to the Authorization page on
     * Dropbox. Ass a fallback it returns the url to that same page.
     *
     * @return
     */
    public String start() {
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.Request.newBuilder().withNoRedirect().build();
        String authorizeUrl = webAuth.authorize(webAuthRequest);
        Logger.getLogger(Auth.class.getName()).log(Level.INFO, "Go to this URL: {0} to authorize \"{1}\"", new Object[]{authorizeUrl, config.getClientIdentifier()});
        try {
            Desktop.getDesktop().browse(new URI(authorizeUrl));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
        return authorizeUrl;
    }

    /**
     * Finish the authroization.
     * <p>
     * Finish the authorization by storing the AccessToken in the users file
     * system.
     *
     * @param accesToken The AccessToken-String provided by Dropbox.
     * @throws HexClientException
     */
    public void finish(String accesToken) throws HexClientException {
        try {
            authFinish = webAuth.finishFromCode(accesToken);
            HexClientProperties.getInstance().setProperty(DBX_ACCESS_TOKEN, authFinish.getAccessToken());
        } catch (DbxException ex) {
            throw new HexClientException("Access Token could not be created", ex);
        }
    }
}
