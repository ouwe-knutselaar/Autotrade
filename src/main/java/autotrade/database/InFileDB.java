package autotrade.database;

import autotrade.pojo.Globals;
import autotrade.pojo.Wallet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class InFileDB {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    private String walletFile;
    private String logFile;
    private String blackListFile;
    private Set<Wallet> walletList = new HashSet<>();
    private LinkedList<String> loglines = new LinkedList<>();
    private LinkedList<String> blackList = new LinkedList<>();
    private static InFileDB INSTANCE;
    private SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    private InFileDB() {
        if(Globals.debug)log.setLevel(Level.DEBUG);
        walletFile = System.getProperty("user.home") + "/.autotrade/walletfile.db";
        blackListFile = System.getProperty("user.home") + "/.autotrade/blacklist.db";
        logFile = System.getProperty("user.home") + "/.autotrade/logfile.log";
        loadWalletListFromFile();
        loadBlackListFromFile();
    }

    public static InFileDB getInstance(){
        if(INSTANCE == null)INSTANCE = new InFileDB();
        return INSTANCE;
    }

    public void addWallet(Wallet wallet){
        for(Wallet checkWallet : walletList)        {
            if(wallet.getCoinId().equals(checkWallet.getCoinId()))return;
        }
        walletList.add(wallet);
        saveWalletListToFile();
    }

    public void deleteWallet(String coinId){
        walletList.removeIf(wallet -> wallet.getCoinId().equals(coinId));
        saveWalletListToFile();
    }

    public void updateWallet(Wallet wallet){
        deleteWallet(wallet.getCoinId());
        addWallet(wallet);
    }

    public Wallet getWallet(String coindId){
        for(Wallet wallet : walletList)        {
            if(wallet.getCoinId().equals(coindId))return wallet;
        }
        return null;
    }

    public List<Wallet> getAllWallets() {
        return new LinkedList<>(walletList);
    }

    private void saveWalletListToFile() {
        try {
            OutputStream os = new FileOutputStream(walletFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(walletList);

            oos.close();
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            Globals.telnetThread=false;
            System.exit(1);
        }
    }

    private void loadWalletListFromFile() {
        walletList.clear();
        try {
            InputStream is = new FileInputStream(walletFile);
            ObjectInputStream ois = new ObjectInputStream(is);
            HashSet<Wallet> workList = (HashSet<Wallet>) ois.readObject();
            walletList.addAll(workList);
            ois.close();
            is.close();
        }catch(FileNotFoundException e)
        {
            log.warn("The data does not exists, recreate "+walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            Globals.telnetThread=false;
            File deleteFile = new File(walletFile);
            deleteFile.delete();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error(walletFile+" is invalid, delete and rebuild");
            Globals.telnetThread=false;
            File deleteFile = new File(walletFile);
            deleteFile.delete();
        }
    }

    public List<String> getNumOfLogRecords(int numberOfLines){
        List<String> workList = new LinkedList<>();
        if(numberOfLines>loglines.size())numberOfLines = loglines.size();
        for(int tel=0;tel<numberOfLines;tel++){
            workList.add(loglines.get(tel));
        }
        return workList;
    }

    public void addLog(String logLine){
        try {
            String time = jdf.format(new Date(Instant.now().toEpochMilli()));
            loglines.addFirst(time+" "+logLine);
            System.out.println(time+" "+logLine);
            File logfile = new File(logFile);
            FileWriter fr = new FileWriter(logfile,true);
            fr.write(time+" "+logLine+System.lineSeparator());
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToBlacklist(String coinid) {
        blackList.add(coinid);
        saveBlackListToFile();
    }

    public void deleteFromBlacklist(String coinToRemove){
        blackList.removeIf(coin -> coin.equals(coinToRemove));
    }

    public boolean isOnTheBlackList(String coinToCheck){
        return blackList.contains(coinToCheck);
    }

    private void saveBlackListToFile() {
        try {
            File fi = new File(blackListFile);
            FileWriter fw = new FileWriter(fi);
            for(String coin:blackList)fw.write(coin+System.lineSeparator());
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
            Globals.telnetThread=false;
            System.exit(1);
        }
    }

    private void loadBlackListFromFile() {
        blackList.clear();
        try {
            InputStream is = new FileInputStream(blackListFile);
            Scanner sc = new Scanner(is);
            while(sc.hasNextLine()){
                blackList.add(sc.nextLine());
            }
            sc.close();
            is.close();
        }catch(FileNotFoundException e)        {
            log.warn("The data does not exists, recreate "+blackListFile);
        } catch (IOException e) {
            e.printStackTrace();
            Globals.telnetThread=false;
            File deleteFile = new File(blackListFile);
            deleteFile.delete();
        }
    }

}
