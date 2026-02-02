package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "domain.entities")
public class DriverrApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverrApplication.class, args);
		System.out.printf("Application initialized");
	}

}
