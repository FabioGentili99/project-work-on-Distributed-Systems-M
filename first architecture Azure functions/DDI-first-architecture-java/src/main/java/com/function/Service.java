package com.function;

public class Service{

    private String id = "";
    private String ServiceAddress = "";

    public Service(){
    }

    public String getId() {
        return id;
    }

    public void setId(String serviceName) {
        id = serviceName;
    }

    public String getServiceAddress() {
        return ServiceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        ServiceAddress = serviceAddress;
    }

    @Override
    public String toString(){
        String result = "id: " + this.getId() + ", address: " + this.getServiceAddress();
        return result;
    }
    
}
