package nu.hex.client.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.hex.client.io.SimpleFileWriter;
import se.digitman.lightxml.DocumentToXmlNodeParser;
import se.digitman.lightxml.NodeFactory;
import se.digitman.lightxml.XmlDocument;
import se.digitman.lightxml.XmlNode;

/**
 * Created 2016-nov-17
 *
 * @author hl
 */
public class HexClientProperties {

    private static final String CLIENT_ROOT_FOLDER = System.getProperty("user.home") + "/.hxcl/";
    private File settingsFile;
    private XmlNode settingsNode;
    public static final String SETTINGS_FOLDER = CLIENT_ROOT_FOLDER + "settings/";
    public static final String SETTINGS_FILE = SETTINGS_FOLDER + "settings.xml";
    public static final String CLIENT_CREDENTIALS_FOLDER = CLIENT_ROOT_FOLDER + ".credentials/";
    public static final String DBX_ACCESS_TOKEN = "dbx-access-token";
    private static final HexClientProperties INSTANCE = new HexClientProperties();

    private HexClientProperties() {
        init();
    }

    public static HexClientProperties getInstance() {
        return INSTANCE;
    }

    private void init() {
        try {
            new File(CLIENT_ROOT_FOLDER).mkdirs();
            new File(SETTINGS_FOLDER).mkdirs();
            settingsFile = new File(SETTINGS_FILE);

            if (settingsFile == null || !settingsFile.exists()) {
                createSettingsFile();
            }
            settingsNode = new DocumentToXmlNodeParser(new FileInputStream(settingsFile)).parse();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HexClientProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getProperty(String propKey) {
        for (XmlNode settingNode : settingsNode.getChildren("setting")) {
            if (settingNode.hasAttribute("key") && settingNode.getAttribute("key").equals(propKey)) {
                return settingNode.getText();
            }
        }
        return null;
    }

    public Integer getPropertyAsInteger(String propKey) {
        try {
            return Integer.valueOf(getProperty(propKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getPropertyAsDouble(String propKey) {
        try {
            return Double.valueOf(getProperty(propKey));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getPropertyAsBoolean(String propKey) {
        return getProperty(propKey) == null
                ? Boolean.FALSE
                : Boolean.valueOf(getProperty(propKey));
    }

    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        settingsNode.getChildren("setting").stream().forEach((settingNode) -> {
            result.put(settingNode.getAttribute("key"), settingNode.getText());
        });
        return result;
    }

    public void setProperty(String propKey, String value) {
        boolean notExists = true;
        for (XmlNode settingNode : settingsNode.getChildren("setting")) {
            if (settingNode.getAttribute("key").equals(propKey)) {
                settingNode.clearText();
                settingNode.addText(value);
                notExists = false;
            }
        }
        if (notExists) {
            addSettingNode(value, propKey);
        }
        writeToSettingsFile();
    }

    public boolean hasProperty(String propKey) {
        return settingsNode.getChildren("setting").stream()
                .anyMatch((settingNode) -> (settingNode.getAttribute("key").equals(propKey)));
    }

    private void addSettingNode(String value, String propKey) {
        XmlNode settingNode = NodeFactory.createNode("setting", value);
        settingNode.addAttribute("key", propKey);
        settingsNode.addChild(settingNode);
    }

    private void writeToSettingsFile() {
        try {
            XmlDocument settingsDoc = new XmlDocument(new DocumentToXmlNodeParser(settingsNode.toString().replaceAll(">\\s*<", "><")).parse());
            new SimpleFileWriter(settingsFile, settingsDoc.toString()).write();
        } catch (IOException ex) {
            Logger.getLogger(HexClientProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSettingsFile() {
        new File(SETTINGS_FOLDER).mkdirs();
        settingsNode = NodeFactory.createNode("settings");
        writeToSettingsFile();
    }
}
