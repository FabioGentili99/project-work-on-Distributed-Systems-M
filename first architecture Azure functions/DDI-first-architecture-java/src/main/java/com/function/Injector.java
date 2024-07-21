package com.function;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.microsoft.azure.functions.ExecutionContext;

public class Injector {
    private static Injector instance;
    private final CosmosContainer container;
    private final ExecutionContext context;

    private Injector(String configFile, ExecutionContext context) {
        CosmosClient client = new CosmosClientBuilder()
                .endpoint("https://gheno.documents.azure.com:443/")
                .key("NwH78y4U7TNbh14kQOVTTPHvqctk2CGtdAdktHf9FqHYcRtSnbVf6kyWHnef70zWQXc73FGMOqsOACDbNYfMqA==")
                .buildClient();

        CosmosDatabase database = client.getDatabase("service-registry");
        this.container = database.getContainer("service-registry2");
        this.context = context;

        if (configFile != null) {
            loadConfig(configFile);
        }
    }

    public static Injector getInstance(String configFile, ExecutionContext context) {
        if (instance == null) {
            instance = new Injector(configFile, context);
        }
        return instance;
    }

    private void loadConfig(String configFile) {
       
        /* 
        try {
             
                //ClassLoader cl = this.getClass().getClassLoader();
                //File file = new File(cl.getResource(configFile).getFile());
            
                //String localFilePath = Paths.get(System.getenv("HOME"), "site", "wwwroot", "sample.xml").toString();
                File file = new File(configFile);
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
                this.context.getLogger().info("" +e);
            }
        */
        registerService("test","localhost");
        registerService("DB","111.222.333.444");
    }

    private void registerService(String serviceName, String serviceUrl) {
        try {
            Service item = new Service();
            item.setServiceAddress(serviceUrl);
            item.setId(serviceName);
            this.container.createItem(item,new PartitionKey(serviceName), new CosmosItemRequestOptions());
        } catch (Exception e) {
            this.context.getLogger().info("Error during service registration: "+ e.getMessage());
        }
    }

    public String getService(String serviceName) {
        String address = "";
        try {
            /* 
            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
            //options.setPartitionKey(new PartitionKey(serviceName));
            String query = "SELECT * FROM c WHERE c.id = @id";
            List<SqlParameter> parameters = Collections.singletonList(new SqlParameter("@id", serviceName));
            SqlQuerySpec sqlQuerySpec = new SqlQuerySpec(query, parameters);
            Iterable<FeedResponse<Service>> result = this.container.queryItems(sqlQuerySpec, options, Service.class).iterableByPage();

            for (FeedResponse<Service> item : result) {
                List<Service> items = item.getResults();
                for (Service x : items){
                    if ( x.getServiceAddress() != null){
                        address =  x.getServiceAddress();
                    } else {
                        address =  null;
                    }
                    
                }
            }
            */
            
             
                CosmosItemResponse<Service> response = this.container.readItem(
                                                                                    serviceName,
                                                                                    new PartitionKey(serviceName),
                                                                                    Service.class
                                                                                );

                //this.context.getLogger().info("response: " +response.toString());
                Service item = response.getItem();
                this.context.getLogger().info("item: " + item.toString());
                if (item.getServiceAddress() != null){
                    address = item.getServiceAddress();
                } else {
                    address = null;
                }
            
        } catch (Exception e) {
            this.context.getLogger().severe( "Error during service retrieval: "+ e.getMessage());
        }
        return address;
        
    }

}

