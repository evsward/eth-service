package com.scph.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    // --rpcapi="db,eth,net,web3,personal,web3"
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
