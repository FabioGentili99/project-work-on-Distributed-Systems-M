package com.function;

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
        //context.getLogger().info("Java HTTP trigger processed a request.");
        //Injector injector = Injector.getInstance("services.xml", context);
        Injector injector = Injector.getInstance("services.xml", context);

        String address = injector.getService("test");


        if (address == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("service not found").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(address).build();
        }
    }
}
