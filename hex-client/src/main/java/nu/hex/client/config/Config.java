package nu.hex.client.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import nu.hex.client.exception.HexClientException;
import nu.hex.client.io.SimpleFileWriter;
import se.digitman.lightxml.DocumentToXmlNodeParser;
import se.digitman.lightxml.NodeFactory;
import se.digitman.lightxml.XmlDocument;
import se.digitman.lightxml.XmlNode;

/**
 * Created 2017-okt-03
 *
 * @author hl
 */
public class Config {

    private static final String CLIENT_ROOT_FOLDER = System.getProperty("user.home") + "/.hxcl/";
    public static final String CONFIG_FOLDER = CLIENT_ROOT_FOLDER + "conf/";
    public static final String CONFIG_FILE = CONFIG_FOLDER + "config.xml";
    private File configFile;
    private static final Config INSTANCE = new Config();
    private XmlDocument confDoc;

    private Config() {
        init();
    }

    private void init() {
        configFile = new File(CONFIG_FILE);
        try {
            if (!configFile.exists()) {
                confDoc = new XmlDocument(NodeFactory.createNode("config"));
                new File(CONFIG_FOLDER).mkdirs();
                configFile = new File(CONFIG_FILE);
                new SimpleFileWriter(configFile, confDoc.toString()).write();
            } else {
                confDoc = new XmlDocument(new DocumentToXmlNodeParser(new FileInputStream(configFile)).parse());
            }
        } catch (IOException ex) {
            throw new HexClientException("Could not create Config File", ex);
        }
    }

    public void save() {
        try {
            new SimpleFileWriter(configFile, confDoc.toString()).write();
        } catch (IOException ex) {
            throw new HexClientException("Could not save file", ex);
        }
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public XmlNode getConfigNode() {
        return confDoc.getRoot();
    }
}
