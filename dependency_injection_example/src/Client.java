@Injectable
public class Client {
    private Service service;

    public Client(){
        this.service = null;
    }

    @Inject
    public Client(Service service){
        this.service = (Service) service;
    }

    public void start(){
        service.doJob();
    }
}

