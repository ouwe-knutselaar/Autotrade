package autotrade.engine.reactive;

import autotrade.database.InFileDB;
import autotrade.network.bitvavo.BitVavoApi;
import autotrade.pojo.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReactiveQueries {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    BitVavoApi tradeApi = new BitVavoApi();
    InFileDB db = InFileDB.getInstance();
    private static ReactiveQueries INSTANCE;

    private ReactiveQueries(){
        if(Globals.debug){
            log.setLevel(Level.DEBUG);
        }
    }

    public static ReactiveQueries getInstance(){
        if(INSTANCE==null)INSTANCE=new ReactiveQueries();
        return INSTANCE;
    }

    public void addWallet(Wallet wallet) {
        db.addWallet(wallet);
    }

    public void deleteWallet(String coinId) {
        db.deleteWallet(coinId);
    }

    public void updateWallet(Wallet wallet) {
        db.updateWallet(wallet);
    }

    public List<Wallet> getAllWalletsFromTheDatabase() {
        return db.getAllWallets();
    }

    public Wallet getWallet(String coinId) {
        return db.getWallet(coinId);
    }

    public double getCoinLimit(String coin) throws EngineExeption {
        Market market = tradeApi.getMarket(coin);
        return market.getMinOrderInBaseAsset();
    }

    public String marketToCoindId(String market) {
        return market.split("-")[0];
    }

    public Order executeSellOrder(String market, double amount) throws EngineExeption {
        return tradeApi.executeSellOrder(market, amount);
    }

    public double getPriceOfCoin(String coinId) throws EngineExeption {
        return tradeApi.getPriceOfCoin(coinId);
    }

    public List<Balance> getWalletsFromTheAPI() throws EngineExeption {
        List<Balance> workList =  tradeApi.getWallets();
        workList.removeIf(balance -> balance.getSymbol().equals("EUR"));    // Remove the EUO
        return workList;
    }

    public List<Candle> getListOfCandlesPeriod(String coinId,int days) throws EngineExeption {
        return tradeApi.getListOfCandlesPeriod(coinId,days);
    }

    public Double getAmountOfEuros() {
        try {
            return tradeApi.getAmountOfEuros();
        }catch (EngineExeption e){
            e.printStackTrace();
            return 0.0;
        }
    }

    public Order executeBuyOrder(String coindId, double amount) throws EngineExeption {
        return tradeApi.executeBuyOrder(coindId, amount);
    }

    public double getPricePaidFor(String coinId) throws EngineExeption {
        List<Trade> tradeList = tradeApi.getListOftradesFor(coinId);
        for (Trade trade : tradeList) {
            if (trade.getSide().equals("buy")) {
                double price = trade.getPrice();
                double amount = trade.getAmount();
                double fee = trade.getFee();
                return (price * amount)+fee;
            }
        }
        return 0;
    }

    public long getBuyMoment(String coinId) throws EngineExeption {
        List<Trade> tradeList = tradeApi.getListOftradesFor(coinId);
        for (Trade trade : tradeList) {
            if (trade.getSide().equals("buy")) {
                return trade.getTimestamp();
            }
        }
        return 0;
    }


    public List<String> printWallets(List<Wallet> walletList) {
        double total = getAmountOfEuros();
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> outList = new LinkedList<>();
        outList.add("+--------+------------+------------+------------+------------+------------+------------+---------+------------------+");
        outList.add("| Coin   | curr value | profit     | high value | paid       | drop       | profit pct | expired |buy date          |");
        outList.add("+--------+------------+------------+------------+------------+------------+------------+---------+------------------+");
        for(Wallet wallet : walletList) {
            String javaDate = jdf.format(new Date(wallet.getEpoch()));
            total +=wallet.getCurrent_value();
            outList.add(String.format("| %-6s | %10.4f | %10.4f | %10.4f | %10.4f | %10.4f | %10.4f | %-7b | %s |",
                    wallet.getCoinId(),
                    wallet.getCurrent_value(),
                    wallet.getPaid() * wallet.getProfit_trigger(),
                    wallet.getHighest_value(),
                    wallet.getPaid(),
                    wallet.getLoss_trigger()*wallet.getHighest_value(),
                    wallet.getProfit_trigger(),
                    wallet.isExpired(),
                    javaDate));
        }
        outList.add("+--------+------------+------------+------------+------------+------------+------------+---------+------------------+");
        outList.add(String.format("| cash   | %10.4f |",getAmountOfEuros() ));
        outList.add("+--------+------------+");
        outList.add(String.format("| total  | %10.4f |",total ));
        outList.add("+--------+------------+");
        outList.forEach(log::debug);
        return outList;
    }

    public List<TickerPrice> getCurrentListOfCoinValues() throws EngineExeption {
        List<TickerPrice> workList = tradeApi.getCurrentListOfCoinValues();             // Get the ticker prizes
        workList.removeIf(tickerPrice -> !tickerPrice.getMarket().contains("EUR"));     // Filter all non euro value
        return workList;
    }

}
