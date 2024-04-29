import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;

public class Injector {
    private final String registryBaseUrl = "http://localhost:8080";
    private final String config_file;

    public Injector(String config_file) {
        this.config_file = config_file;
        if (this.config_file != null) {
            try {
                File file = new File(config_file);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // Method to register a service with the registry
    public void registerService(String serviceName, String serviceUrl) throws IOException {
        String endpoint = registryBaseUrl + "/register/" + serviceName;
        sendRequest(endpoint, "POST", serviceUrl);
    }

    // Method to discover a service from the registry
    public String getService(String serviceName) throws IOException {
        String endpoint = registryBaseUrl + "/discover/" + serviceName;
        return sendRequest(endpoint, "GET", null);
    }

    // Helper method to send HTTP requests
    private String sendRequest(String endpoint, String method, String payload) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "text/plain");

        if (payload != null) {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }

}