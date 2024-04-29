import java.io.IOException;

public class Main {
    public static void main(String[] args){
        Injector inj = new Injector(null);
        try {
            inj.registerService("test", "localhost");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String testAddress = inj.getService("test");
            System.out.println(testAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return;

    }
}
