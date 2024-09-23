import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;



public class Injector {
    private String DYNAMODB_TABLE_NAME = "service-registry2";
    private static Injector _instance = null;
    private DynamoDbClient connection;

    private Injector() {
        this.connection = DynamoDbClient.builder()
                .region(Region.EU_NORTH_1)
                .build();
    }

    public static Injector getInstance() {
        if (_instance == null) {
            _instance = new Injector();
        }
        return _instance;
    }

    public void registerService(String id, String serviceName, String serviceAddress) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
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

    public Map<String, AttributeValue> getService(String id) {

        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("id", AttributeValue.builder()
                .s(id)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(DYNAMODB_TABLE_NAME)
                .build();

        Map<String, AttributeValue> returnedItem = new HashMap<>();
        try {

    
            long start = System.currentTimeMillis();
            // If there is no matching item, GetItem does not return any data.
            returnedItem = this.connection.getItem(request).item();
            long end = System.currentTimeMillis();
            long duration = end - start;
            System.out.println("Read from DynamoDB table executed in " + (duration) + " ms");
            if (returnedItem.isEmpty())
                System.out.format("No item found with the key %s!\n", id);
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
        return returnedItem;
    }
}


