package configuration;

import domain.User;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import web.servlet.UserServlet;

import static java.util.Optional.ofNullable;


public class ServerConfigurationFactory {
    private static Server server;

    public static Server getServer(final int port) {
        return ofNullable(server)
                .orElseGet(() -> {
                    return server = createServer(port);
                });
    }

    private static Server createServer(final int port) {
        final var server = new Server(port);
        final ServletHandler servletHandler = createServletHandler();
        final ResourceHandler resourceHandler = createResourceHandler();
        final ContextHandler contextHandler = new ContextHandler("/");
        contextHandler.setHandler(resourceHandler);
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{contextHandler, servletHandler});
        server.setHandler(handlers);
        return server;
    }

    private static ServletHandler createServletHandler() {
        final var serviceConfigurationFactory = new ServiceConfigurationFactory<User>();
        final var dbService = serviceConfigurationFactory.getDbService(User.class);
        dbService.create(User.builder().age(12).name("asdef").build());
        final ServletHandler servletHandler = new ServletHandler();
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
}
