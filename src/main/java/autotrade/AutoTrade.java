package autotrade;

import autotrade.engine.reactive.EngineExeption;
import autotrade.engine.reactive.ReactiveEngine;
import autotrade.network.TelnetServer;
import autotrade.network.webserver.JettyServer;
import autotrade.pojo.Globals;
import java.io.IOException;

public class AutoTrade {

    public static final String VERSION = "0.1";

    public static void main(String[] argv) throws IOException {
        AutoTrade autotrade = new AutoTrade();

        String configfile = System.getProperty("user.home") + "/.autotrade/autotrade.properties";
        if (argv.length != 0) configfile = argv[0];
        Globals.loadConfig(configfile);
        autotrade.startEgnine();
    }

    public AutoTrade() {
        System.out.println("AutoTrade " + VERSION);
    }

    public void startEgnine() {
        ReactiveEngine engine = new ReactiveEngine();
        try {

            TelnetServer telnetServer = new TelnetServer();
            Thread telnetServerThread = new Thread(telnetServer);
            telnetServerThread.start();

            JettyServer jettyServer = new JettyServer();
            Thread jettyServerThread = new Thread(jettyServer);
            jettyServerThread.start();

            engine.init();
            engine.loop();
        } catch (EngineExeption engineExeption) {
            engineExeption.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Globals.telnetThread = false;
        }
    }

}
