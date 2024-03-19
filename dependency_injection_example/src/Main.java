import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {
        Injector injector = new Injector();
        injector.register(Service.class, new MyService());

        Client client;
        try {
            client = (Client) injector.inject(Client.class);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        client.start();
    }
}
