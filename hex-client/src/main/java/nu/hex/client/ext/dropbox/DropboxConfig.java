package nu.hex.client.ext.dropbox;

import nu.hex.client.config.Config;
import nu.hex.client.exception.HexClientException;
import se.digitman.lightxml.NodeFactory;
import se.digitman.lightxml.XmlNode;

/**
 * Created 2017-okt-06
 *
 * @author hl
 */
public class DropboxConfig {

    private static final String APP_KEY = "app-key";
    private static final String SECRET = "secret";
    private static final String APP_NODE_NAME = "application";
    private XmlNode dbNode;
    private final Config conf = Config.getInstance();

    public DropboxConfig(String clientIdentifier, String appRoot) {
        init(clientIdentifier, appRoot);
    }

    private void init(String clientIdentifier, String appRoot) {
        if (appNodeExists(clientIdentifier)) {
            dbNode = conf.getConfigNode().getChild(APP_NODE_NAME);
        } else {
            dbNode = NodeFactory.createNode(APP_NODE_NAME);
            conf.getConfigNode().addChild(dbNode);
        }
        dbNode.addAttribute("id", clientIdentifier);
        dbNode.addAttribute("root-folder", appRoot);
    }

    private boolean appNodeExists(String clientIdentifier) {
        return conf.getConfigNode().getChildren(APP_NODE_NAME).stream().anyMatch((node) -> (node.hasAttribute("id") && node.getAttribute("id").equals(clientIdentifier)));
    }

    public String getAppKey() {
        return dbNode != null && dbNode.hasChildNamed(APP_KEY) ? dbNode.getChild(APP_KEY).getText() : null;
    }

    public void setAppKey(String appKey) {
        if (dbNode == null) {
            throw new HexClientException("Could not save AppKey", new NullPointerException("No parent node found"));
        }
        if (dbNode.hasChildNamed(APP_KEY)) {
            dbNode.getChild(APP_KEY).clearText();
            dbNode.getChild(APP_KEY).addText(appKey);
        } else {
            dbNode.addChild(NodeFactory.createNode(APP_KEY, appKey));
        }
    }

    public String getSecret() {
        return dbNode != null && dbNode.hasChildNamed(SECRET) ? dbNode.getChild(SECRET).getText() : null;
    }

    public void setSecret(String secret) {
        if (dbNode == null) {
            throw new HexClientException("Could not save Secret", new NullPointerException("No parent node found"));
        }
        if (dbNode.hasChildNamed(SECRET)) {
            dbNode.getChild(SECRET).clearText();
            dbNode.getChild(SECRET).addText(secret);
        } else {
            dbNode.addChild(NodeFactory.createNode(SECRET, secret));
        }
    }

    public String getClientIdentifier() {
        return dbNode != null && dbNode.hasAttribute("id") ? dbNode.getAttribute("id") : null;
    }

    public String getAppRootFolder() {
        return dbNode != null && dbNode.hasAttribute("root-folder") ? "/" + dbNode.getAttribute("root-folder") : null;
    }
}
