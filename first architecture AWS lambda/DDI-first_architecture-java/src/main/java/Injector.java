import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Injector {
    private String DYNAMODB_TABLE_NAME = "service-registry";
    private static Injector _instance = null;
    private Map<String, String> registry;
    private String configFile;
    private DynamoDbClient connection;

    private Injector(String configFile) {
        this.configFile = configFile;
        this.connection = DynamoDbClient.builder()
                .region(Region.EU_NORTH_1)
                .build();

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
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("ServiceName", AttributeValue.builder().s(serviceName).build());
        item.put("ServiceAddress", AttributeValue.builder().s(serviceAddress).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .item(item)
                .build();
        try {
            PutItemResponse response = this.connection.putItem(request);
            System.out.println(DYNAMODB_TABLE_NAME + " was successfully updated. The request id is "
                    + response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", DYNAMODB_TABLE_NAME);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public String getService(String serviceName) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("ServiceName", AttributeValue.builder()
                .s(serviceName)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(DYNAMODB_TABLE_NAME)
                .build();

        Map<String, AttributeValue> returnedItem = new HashMap<>();
        try {
            // If there is no matching item, GetItem does not return any data.
            returnedItem = this.connection.getItem(request).item();
            if (returnedItem.isEmpty())
                System.out.format("No item found with the key %s!\n", serviceName);
            else {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");
                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }

            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return returnedItem.get("ServiceAddress").s();//toString();
    }
}


