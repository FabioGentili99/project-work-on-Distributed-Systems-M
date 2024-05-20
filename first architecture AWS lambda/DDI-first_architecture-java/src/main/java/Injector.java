import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Injector {
    private static Injector _instance = null;
    private Map<String, String> registry;
    private String configFile;

    private Injector(String configFile) {
        this.registry = new HashMap<>();
        this.configFile = configFile;

        if (this.configFile != null) {
            try {
                ClassLoader cl = getClass().getClassLoader();
                File file = new File(cl.getResource(this.configFile).getFile());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("service");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String name = element.getElementsByTagName("name").item(0).getTextContent();
                        String address = element.getElementsByTagName("address").item(0).getTextContent();
                        registerService(name, address);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                System.err.println(e);
            }
        }
    }

    public static Injector getInstance(String configFile) {
        if (_instance == null) {
            _instance = new Injector(configFile);
        }
        return _instance;
    }

    public void registerService(String serviceName, String serviceAddress) {
        this.registry.put(serviceName, serviceAddress);
        System.out.println("Service " + serviceName + " registered successfully.");
    }

    public String getService(String serviceName) {
        String response = this.registry.get(serviceName);
        if (response != null) {
            return response;
        } else {
            System.err.println("Failed to discover service " + serviceName);
            return null;
        }
    }
}


