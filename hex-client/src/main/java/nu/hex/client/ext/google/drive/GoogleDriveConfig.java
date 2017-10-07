package nu.hex.client.ext.google.drive;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nu.hex.client.config.Config;
import se.digitman.lightxml.NodeFactory;
import se.digitman.lightxml.XmlNode;

/**
 * Created 2017-okt-07
 *
 * @author hl
 */
public class GoogleDriveConfig {

    private static final String GOOGLE_NODE_NAME = "google";
    private static final String DRIVE_NODE_NAME = "drive";
    private static final String APPLICATION_NODE_NAME = "application";
    private static final String SCOPES_NODE_NAME = "scopes";
    private static final String SCOPE_NODE_NAME = "scope";
    private static final String ROOT_FOLDER = "root-folder";
    private static final String CLIENT_IDENTIFIER = "id";
    private static final String ROOT_ID = "root-id";
    private static final String FOLDER_COLOR = "folder-color";

    private XmlNode appNode;
    private XmlNode googleNode;
    private final Config conf = Config.getInstance();

    public GoogleDriveConfig(String clientIdentifier, String appRoot) {
        init(clientIdentifier, appRoot);
    }

    private void init(String clientIdentifier, String appRoot) {
        if (!conf.getConfigNode().hasChildNamed(GOOGLE_NODE_NAME)) {
            googleNode = NodeFactory.createNode(GOOGLE_NODE_NAME);
            conf.getConfigNode().addChild(googleNode);
        } else {
            googleNode = conf.getConfigNode().getChild(GOOGLE_NODE_NAME);
        }
        if (!googleNode.hasChildNamed(DRIVE_NODE_NAME)) {
            googleNode.addChild(NodeFactory.createNode(DRIVE_NODE_NAME));
        }
        appNode = getAppNode(clientIdentifier);
        if (appNode == null) {
            appNode = NodeFactory.createNode(APPLICATION_NODE_NAME);
            appNode.addAttribute(CLIENT_IDENTIFIER, clientIdentifier);
            appNode.addAttribute(ROOT_FOLDER, appRoot);
            googleNode.getChild(DRIVE_NODE_NAME).addChild(appNode);
        }
    }

    private XmlNode getAppNode(String clientIdentifier) {
        if (googleNode.getChild(DRIVE_NODE_NAME).hasChildNamed(APPLICATION_NODE_NAME)) {
            for (XmlNode n : googleNode.getChild(DRIVE_NODE_NAME).getChildren(APPLICATION_NODE_NAME)) {
                if (n.hasAttribute(CLIENT_IDENTIFIER) && n.getAttribute(CLIENT_IDENTIFIER).equals(clientIdentifier)) {
                    return n;
                }
            }
        }
        return null;
    }

    public Optional<String> getClientIdentifier() {
        if (appNode.hasAttribute(CLIENT_IDENTIFIER)) {
            return Optional.of(appNode.getAttribute(CLIENT_IDENTIFIER));
        }
        return Optional.empty();
    }

    public Optional<String> getRootFolder() {
        if (appNode.hasAttribute(ROOT_FOLDER)) {
            return Optional.of(appNode.getAttribute(ROOT_FOLDER));
        }
        return Optional.empty();
    }

    public void setRootFolderId(String id) {
        appNode.addAttribute(ROOT_ID, id);
    }

    public Optional<String> getRootId() {
        if (appNode.hasAttribute(ROOT_ID)) {
            return Optional.of(appNode.getAttribute(ROOT_ID));
        }
        return Optional.empty();
    }

    public void setFolderColorRGB(String rgb) {
        appNode.addAttribute(FOLDER_COLOR, rgb);
    }

    public Optional<String> getFolderColorRGB() {
        if (appNode.hasAttribute(FOLDER_COLOR)) {
            return Optional.of(appNode.getAttribute(FOLDER_COLOR));
        }
        return Optional.empty();
    }

    public String save() {
        return conf.save();
    }

    public void addScope(String key, String value) {
        if (!appNode.hasChildNamed(SCOPES_NODE_NAME)) {
            appNode.addChild(NodeFactory.createNode(SCOPES_NODE_NAME));
        }
        for (XmlNode sn : appNode.getChild(SCOPES_NODE_NAME).getChildren(SCOPE_NODE_NAME)) {
            if (sn.getAttribute("key").equals(key)) {
                sn.clearText();
                sn.addText(value);
                return;
            }
        }
        XmlNode sn = NodeFactory.createNode(SCOPE_NODE_NAME, value);
        sn.addAttribute("key", key);
        appNode.getChild(SCOPES_NODE_NAME).addChild(sn);
    }

    public List<String> getScopes() {
        List<String> scopes = new ArrayList<>();
        appNode.getChild(SCOPES_NODE_NAME).getChildren(SCOPE_NODE_NAME).forEach((n) -> {
            scopes.add(n.getText());
        });
        return scopes;
    }
}
