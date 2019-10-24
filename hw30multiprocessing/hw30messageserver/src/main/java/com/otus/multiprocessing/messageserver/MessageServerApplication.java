package com.otus.multiprocessing.messageserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class MessageServerApplication {
    public static void main(String[] args) {
        run(MessageServerApplication.class, args);
    }
}
