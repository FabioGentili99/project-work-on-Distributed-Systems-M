package it.unibo.registry.SpringRegistryService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class SpringRegistryServiceApplication {

	private Map<String, String> serviceRegistry = new HashMap<>();

	@PostMapping("/register/{serviceName}")
	public ResponseEntity<String> registerService(@PathVariable String serviceName, @RequestBody String address){
		serviceRegistry.put(serviceName, address);
		return ResponseEntity.ok("Service registered successfully.");
	}


	@GetMapping("/discover/{serviceName}")
	public ResponseEntity<String> discoverService(@PathVariable String serviceName) {
		String serviceAddress = serviceRegistry.get(serviceName);
		if (serviceAddress != null) {
			return ResponseEntity.ok(serviceAddress);
		} else {
			return (ResponseEntity<String>) ResponseEntity.notFound();
		}
	}


	@GetMapping("/delete/{serviceName}")
	public ResponseEntity<String> deleteService(@PathVariable String serviceName) {
		serviceRegistry.remove(serviceName);
		return ResponseEntity.ok("the service has succesfully been deleted");
	}


	@PostMapping("/update/{serviceName}")
	public ResponseEntity<String> updateService(@PathVariable String serviceName, @RequestBody String address){
		serviceRegistry.put(serviceName, address);
		return ResponseEntity.status(HttpStatus.CREATED).body("Service updated successfully.");
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringRegistryServiceApplication.class, args);
	}

}
