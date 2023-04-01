package pl.rb.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CryptoManagerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(CryptoManagerApplication.class, args);
    }

}
