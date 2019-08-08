import configuration.ServerConfigurationFactory;
import org.eclipse.jetty.server.Server;

public class Application {
    public static void main(String[] args) throws Exception {
        final Server server = ServerConfigurationFactory.getServer(8080);
        server.start();
        server.join();
    }
}
