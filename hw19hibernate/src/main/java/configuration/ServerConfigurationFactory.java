package configuration;

import domain.User;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
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
        final var serviceConfigurationFactory = new ServiceConfigurationFactory<User>();
        final var dbService = serviceConfigurationFactory.getDbService(User.class);
        final var context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new UserServlet(dbService)), "/user");

        final var server = new Server(port);
        server.setHandler(new HandlerList(context));

        return server;
    }
}
