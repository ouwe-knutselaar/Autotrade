package autotrade;


import autotrade.engine.reactive.EngineExeption;
import autotrade.engine.reactive.ReactiveQueries;
import autotrade.network.TelnetServer;
import autotrade.pojo.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*
  swing = Kijk welke coins de grootste swing hebben (verschill tussen hoog en laag in pct van het gemiddelde) ofwel de grootste schommeling
  depth = kijk welke coins het laagste staat ten opzichte van het gemiddelde. Waarbij de current onder het gemiddelde is

  interesse = swing*|depth|

  hoogste interesse wordt gekocht

 */
public class Test {

    ReactiveQueries rq;
    Gson gson = new Gson();


    public static void main(String[] argv) throws Exception {

        Test t = new Test(argv);
    }

    public Test(String[] argv) throws Exception {
        System.out.println("TEST SOFTWARE");
        String configfile = System.getProperty("user.home") + "/.autotrade/autotrade.properties";
        if (argv.length != 0) configfile = argv[0];
        Globals.loadConfig(configfile);
        rq = ReactiveQueries.getInstance();

        //List<Candle> candleList = readCandles("C:\\erwin\\IdeaProjects\\Autotrade\\src\\main\\resources\\candles.json");
        //TelnetServer te =new TelnetServer();
        //te.run();

        double x = (22.0/7.0)*1000;
        System.out.println(r(x,4));
        System.out.printf("Test | %10.4f | \n",x);
    }

    String r(double x,int comma)
    {
        DecimalFormat df = new DecimalFormat("#.####");
        return df.format(x);
    }


    private void wait(int seconds) {
        try {
            for (int tel = 0; tel < seconds; tel = tel + 5) {
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
