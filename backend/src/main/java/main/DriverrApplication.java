package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
	"main",
	"controller",
	"service",
	"repository",
	"config",
	"domain",
	"dto",
	"utils"
})
@EntityScan(basePackages = "domain.entities")
@EnableJpaRepositories(basePackages = "repository")
public class DriverrApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriverrApplication.class, args);
		System.out.printf("Application initialized");
	}

}
