package com.otus.java.ioc.configuration;

import com.otus.java.ioc.web.handler.ServerResourceHandler;
import com.otus.java.ioc.web.handler.ServerSecurityHandler;
import com.otus.java.ioc.web.handler.ServerServletHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.otus.java.ioc.web")
public class ServerConfiguration {
    private final static int PORT = 8080;

    @Bean
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
}
