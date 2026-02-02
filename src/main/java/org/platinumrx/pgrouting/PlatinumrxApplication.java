package org.platinumrx.pgrouting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlatinumrxApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatinumrxApplication.class, args);
    }

}
