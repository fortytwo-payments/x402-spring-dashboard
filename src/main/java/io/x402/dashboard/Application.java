package io.x402.dashboard;

import io.x402.dashboard.seller.annotation.EnableX402Dashboard;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableX402Dashboard
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
