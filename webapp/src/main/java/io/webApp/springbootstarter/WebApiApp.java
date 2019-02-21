package io.webApp.springbootstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.webApp.springbootstarter.fileStorage.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class WebApiApp {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(WebApiApp.class, args);
	}

}
