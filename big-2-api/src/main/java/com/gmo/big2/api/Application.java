package com.gmo.big2.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author tedelen
 */
@SpringBootApplication(scanBasePackages = { "com.gmo.big2.api.config", "com.gmo.big2.api.controller" })
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
