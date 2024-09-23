package com.function;

public class Service{

    private String id;
    private String ServiceName;
    private String ServiceAddress;

    public Service(){
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getServiceName(){
        return this.ServiceName;
    }

    public void setServiceName(String ServiceName){
        this.ServiceName = ServiceName;
    }

    public String getServiceAddress() {
        return this.ServiceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.ServiceAddress = serviceAddress;
    }

    @Override
    public String toString(){
        String result = "id: " + this.getId() + ", name: " + this.getServiceName() + ", address: " + this.getServiceAddress();
        return result;
    }
    
}
