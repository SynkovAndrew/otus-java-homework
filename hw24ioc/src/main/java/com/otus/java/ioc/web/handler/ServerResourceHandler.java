package com.otus.java.ioc.web.handler;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.util.Optional.ofNullable;

@Component
public class ServerResourceHandler extends ResourceHandler {
    @PostConstruct
    public void init() {
        ofNullable(ServerResourceHandler.class.getClassLoader().getResource("WEB-INF"))
                .ifPresentOrElse(url -> {
                    setDirectoriesListed(false);
                    setWelcomeFiles(new String[]{"index.html"});
                    setResourceBase(url.getPath());
                }, () -> {
                    throw new RuntimeException("Failed to find static resources!");
                });
    }
}
