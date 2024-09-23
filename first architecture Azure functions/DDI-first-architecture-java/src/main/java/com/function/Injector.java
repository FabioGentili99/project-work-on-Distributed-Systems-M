package com.function;



import java.util.logging.Logger;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.microsoft.azure.functions.ExecutionContext;


public class Injector {
    private static Injector instance;
    private final CosmosContainer container;
    private Logger logger;
    private final String endpoint = "https://registry.documents.azure.com:443/";
    private final String key = "xegBV4RBjOaCvIUAKLeg1aBwL2kqQGtcJDH5HpMLfAJrgat8l4gnbUVkPg9ouwUv3ZxB7lL98lDIACDbejdGJg==";

    private Injector(ExecutionContext context) {
        CosmosClient client = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .buildClient();

        CosmosDatabase database = client.getDatabase("service-registry");
        this.container = database.getContainer("service-registry4");    
        this.logger = context.getLogger();    
    }

    public static Injector getInstance(ExecutionContext context) {
        if (instance == null) {
            instance = new Injector(context);
            return instance;
        } else {
            instance.logger = context.getLogger();
            return instance;
        }
    }


    private void registerService(String id, String serviceName, String serviceUrl) {
        try {
            Service item = new Service();
            item.setServiceAddress(serviceUrl);
            item.setId(id);
            item.setServiceName(serviceName);
            this.container.createItem(item,new PartitionKey(id), new CosmosItemRequestOptions());
        } catch (Exception e) {
            this.logger.info("Error during service registration: "+ e.getMessage());
        }
    }

    public Service getService(String id) {
        try {
            /* 
            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
            //options.setPartitionKey(new PartitionKey(id));
            String query = "SELECT * FROM c WHERE c.id = @id";
            List<SqlParameter> parameters = Collections.singletonList(new SqlParameter("@id", id));
            SqlQuerySpec sqlQuerySpec = new SqlQuerySpec(query, parameters);

            long start = System.currentTimeMillis();
            Iterable<FeedResponse<Service>> result = this.container.queryItems(sqlQuerySpec, options, Service.class).iterableByPage();
            long end = System.currentTimeMillis();

            this.context.getLogger().log(Level.INFO, "Read from DynamoDB table executed in " + (end-start) + " ms");

            for (FeedResponse<Service> item : result) {
                List<Service> items = item.getResults();
                for (Service x : items){
                    if ( x.getId().equals(id)){
                        service = x;
                    }
                    
                }
            }
            */
                long start = System.currentTimeMillis();
                Service service = this.container.readItem(
                                                                                    id,
                                                                                    new PartitionKey(id),
                                                                                    Service.class
                                                                                ).getItem();

                long end = System.currentTimeMillis();
                this.logger.info("Read from DynamoDB table executed in " + (end-start) + " ms");
                //this.context.getLogger().info("response: " +response.toString());

            
                this.logger.info("service: " + service.toString());
                return service;
            
        } catch (Exception e) {
            this.logger.info("Error during service retrieval: "+ e.getMessage());
        }
        return new Service();
        
    }

}

