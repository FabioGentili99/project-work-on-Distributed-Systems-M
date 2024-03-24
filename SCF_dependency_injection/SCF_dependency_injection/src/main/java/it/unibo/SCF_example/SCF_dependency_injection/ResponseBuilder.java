package it.unibo.SCF_example.SCF_dependency_injection;

import org.springframework.stereotype.Service;

@Service
public class ResponseBuilder {

    private StringBuilder sb;
    private final String greeting = "hello ";

    public ResponseBuilder(){
        this.sb = new StringBuilder();
    }

    public String getResponse(String value){
        return sb.append(greeting).append(value).toString();
    }
}
