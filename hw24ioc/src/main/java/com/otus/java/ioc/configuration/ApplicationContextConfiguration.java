package com.otus.java.ioc.configuration;

import com.google.gson.Gson;
import com.otus.java.ioc.web.handler.ServerResourceHandler;
import com.otus.java.ioc.web.handler.ServerSecurityHandler;
import com.otus.java.ioc.web.handler.ServerServletHandler;
import domain.Address;
import domain.Phone;
import domain.User;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
@ComponentScan(basePackages = "com.otus.java.ioc")
public class ApplicationContextConfiguration {
    private final static int PORT = 8080;

    @Bean
    @Autowired
    public Server server(final @Autowired ServerServletHandler servletHandler,
                         final @Autowired ServerResourceHandler resourceHandler,
                         final @Autowired ServerSecurityHandler securityHandler) {
        final var server = new Server(PORT);
        final var contextHandler = new ContextHandler("/");
        contextHandler.setHandler(new HandlerList(resourceHandler));
        securityHandler.setHandler(new HandlerList(contextHandler, servletHandler));
        final var handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{securityHandler});
        server.setHandler(handlers);
        return server;
    }

    @Bean
    public SessionFactory sessionFactory() {
        final var configuration = new org.hibernate.cfg.Configuration().configure("hibernate.cfg.xml");
        final var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        Stream.of(Address.class, Phone.class, User.class).forEach(metadataSources::addAnnotatedClass);
        final Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
