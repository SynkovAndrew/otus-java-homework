package configuration;

import domain.User;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import web.servlet.UserServlet;

import java.util.Collections;

import static java.util.Optional.ofNullable;


public class ServerConfigurationFactory {
    private static Server server;

    public static Server getServer(final int port) {
        return ofNullable(server)
                .orElseGet(() -> server = createServer(port));
    }

    private static Server createServer(final int port) {
        final var server = new Server(port);
        final var servletHandler = createServletHandler();
        final var resourceHandler = createResourceHandler();
        final var contextHandler = new ContextHandler("/");
        contextHandler.setHandler(new HandlerList(resourceHandler));
        final var securityHandler = createSecurityHandler();
        securityHandler.setHandler(new HandlerList(contextHandler, servletHandler));
        final var handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{securityHandler});
        server.setHandler(handlers);
        return server;
    }

    private static ServletHandler createServletHandler() {
        final var serviceConfigurationFactory = new ServiceConfigurationFactory<User>();
        final var dbService = serviceConfigurationFactory.getDbService(User.class);
        final var servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new UserServlet(dbService)), "/user");
        return servletHandler;
    }

    private static ResourceHandler createResourceHandler() {
        return ofNullable(ServerConfigurationFactory.class.getClassLoader().getResource("static"))
                .map(url -> {
                    final var resourceHandler = new ResourceHandler();
                    resourceHandler.setDirectoriesListed(false);
                    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
                    resourceHandler.setResourceBase(url.getPath());
                    return resourceHandler;
                })
                .orElseThrow(() -> new RuntimeException("Failed to find static resources!"));

    }

    private static SecurityHandler createSecurityHandler() {
        final var resource = ServerConfigurationFactory.class.getClassLoader().getResource("realm.properties");
        return ofNullable(resource)
                .map(res -> {
                    final var constraint = new Constraint("auth", "admin");
                    constraint.setAuthenticate(true);
                    final var constraintMapping = new ConstraintMapping();
                    constraintMapping.setPathSpec("/*");
                    constraintMapping.setConstraint(constraint);
                    final var constraintSecurityHandler = new ConstraintSecurityHandler();
                    constraintSecurityHandler.setAuthenticator(new BasicAuthenticator());
                    constraintSecurityHandler.setLoginService(new HashLoginService("MyRealm", res.getPath()));
                    constraintSecurityHandler.setConstraintMappings(Collections.singletonList(constraintMapping));
                    return constraintSecurityHandler;
                })
                .orElseThrow(() -> new RuntimeException("Realm properties is not found!"));
    }
}
