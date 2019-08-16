package com.otus.java.ioc;

import com.otus.java.ioc.configuration.ApplicationContextConfiguration;
import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) throws Exception {
        final var context = new AnnotationConfigApplicationContext(ApplicationContextConfiguration.class);
        final var server = context.getBean(Server.class);
        server.start();
        server.join();
    }
}
