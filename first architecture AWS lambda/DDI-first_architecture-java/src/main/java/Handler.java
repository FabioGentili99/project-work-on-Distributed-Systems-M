import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class Handler implements RequestHandler<Object, Response>{

    public Response handleRequest(Object input, Context context) {
        Injector injector = Injector.getInstance("services.xml");
        String serviceAddress = injector.getService("test");
        if (serviceAddress!=null) {
            return new Response(serviceAddress,200);
        }
        else return new Response("service not found",404);
    }
}
