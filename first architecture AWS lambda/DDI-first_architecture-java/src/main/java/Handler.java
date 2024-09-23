import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


public class Handler implements RequestHandler<Object, Response>{

    public Response handleRequest(Object input, Context context) {

        try {
            Map<String, AttributeValue> service = new HashMap<>();
            
            Injector injector = Injector.getInstance();
            service = injector.getService("hello");
            

            System.out.println("hello world function address: " + service.get("ServiceAddress").s());
            
            URL url;
            url = new URL(service.get("ServiceAddress").s());
            
            long start = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            con.disconnect();
            long end = System.currentTimeMillis();
            long duration = end - start;
            
            System.out.println("hello world function executed in " + (duration) + " ms");
            
            
        } catch (IOException ex) {
            return new Response("error during execution",500);
        }

        return new Response("execution completed with success",200);

    }
}
