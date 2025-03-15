package br.com.gerenciador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GerenciadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GerenciadorApplication.class, args);
    }

}
