package com.function;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    //private final Injector injector = Injector.getInstance("services.xml");

    @FunctionName("DDI-first-architecture-java")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            //context.getLogger().info("Java HTTP trigger processed a request.");
            //Injector injector = Injector.getInstance("services.xml", context);
            Injector injector = Injector.getInstance(context);
            Service helloService = injector.getService("hello");
            
            URL url;
            url = new URL(helloService.getServiceAddress());
            long start = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.getResponseCode();
            con.disconnect();
            long end = System.currentTimeMillis();
            
            long duration = end - start;
            context.getLogger().info("hello world function executed in " + (duration) + " ms");
            
            
        } catch (MalformedURLException ex) {
            return request.createResponseBuilder(HttpStatus.OK).body("error during execution").build();
        } catch (IOException e) {
            return request.createResponseBuilder(HttpStatus.OK).body("error during execution").build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body("execution completed with success").build();
        
    }
}
