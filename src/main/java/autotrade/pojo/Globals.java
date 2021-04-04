package autotrade.pojo;

import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Globals {

    public static String apikey = "xxxx";
    public static String apisecret = "xxxx";
    public static int interval = 60;
    public static double lowlimit = 0.9;
    public static double fee = 0.0025;
    public static boolean debug = false;
    public static boolean demo = false;
    public static double default_paid=10;
    public static double profit_trigger=1.02;
    public static double spike_trigger=1.1;
    public static boolean telnetThread = true;
    public static String htmlLogFile;
    public static int profitExpirationTime = 168;        // The time in hours that a coin must make profit.
    public static int maxAmountOfwallets = 10;          // maximum number of wallets


    public static void loadConfig(String configFile) throws IOException {
        final Logger log = Logger.getLogger(Globals.class.getSimpleName());
        log.info("Load config from "+configFile);

        Properties prop = new Properties();
        InputStream inputStream = new FileInputStream(configFile);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + configFile + "' not found");
        }

        Globals.apikey = prop.getProperty("apikey","NONE");
        Globals.apisecret = prop.getProperty("apisecret","NONE");
        Globals.interval = Integer.parseInt(prop.getProperty("interval","60"));
        Globals.lowlimit = Double.parseDouble(prop.getProperty("lowlimit","0.9"));
        Globals.fee = Double.parseDouble(prop.getProperty("fee","0.0025"));
        Globals.debug = Boolean.parseBoolean(prop.getProperty("debug","false"));
        Globals.demo = Boolean.parseBoolean(prop.getProperty("demo","false"));
        Globals.default_paid = Double.parseDouble(prop.getProperty("default_paid","10"));
        Globals.profit_trigger = Double.parseDouble(prop.getProperty("profit_trigger","1.02"));
        Globals.spike_trigger = Double.parseDouble(prop.getProperty("spike_trigger","1.1"));
        Globals.htmlLogFile = prop.getProperty("html_log","autotrade.html");
        Globals.profitExpirationTime = Integer.parseInt(prop.getProperty("expire","168"));
        Globals.maxAmountOfwallets = Integer.parseInt(prop.getProperty("max_wallets","10"));

        log.info("Interval in seconds is "+Globals.interval);
        log.info("Coin expires after "+Globals.profitExpirationTime+" hours");
        log.info("Default low tigger "+Globals.lowlimit+" of the highest value");
        log.info("Default profit tigger "+Globals.profit_trigger+" of the paid price");
        log.info("Default spike tigger "+Globals.spike_trigger+" of the paid price");
        log.info("Default paid price "+Globals.default_paid+" €");
        log.info("Maximum amount of wallets "+Globals.maxAmountOfwallets);
        log.info("HTML logfile "+Globals.htmlLogFile);
        log.info("Demo mode is "+Globals.demo);
        log.info("Debug is "+Globals.debug);

        inputStream.close();

    }
}
