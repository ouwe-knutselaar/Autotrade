package autotrade.network.webserver;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyServer implements Runnable{

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private Server server;

    public void startWebServer() throws Exception {
        log.info("Start the webserver");
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[] {connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(AutotradeServlet.class, "/");

        server.start();
        server.join();
    }

    @Override
    public void run() {
        try {
            startWebServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
