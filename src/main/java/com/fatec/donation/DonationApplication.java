package com.fatec.donation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableNeo4jAuditing
public class DonationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonationApplication.class, args);
	}

}
