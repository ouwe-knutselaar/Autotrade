package autotrade.engine.reactive;

import autotrade.database.InFileDB;
import autotrade.pojo.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReactiveEngine {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private boolean loop = true;
    private final ReactiveQueries rq = ReactiveQueries.getInstance();
    private final InFileDB db = InFileDB.getInstance();
    DecimalFormat df = new DecimalFormat("#.####");

    public ReactiveEngine() {
        if (Globals.debug) log.setLevel(Level.DEBUG);
    }

    public void init() throws EngineExeption {
        db.addLog("Initialize the Reactive engine");
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        rq.printWallets(updateInternalValuesOfTheWallets());
    }

    public void loop() {
        db.addLog("Start the loop");
        while (loop) {
            try {
                wait(Globals.interval);
                List<Balance> balanceList = rq.getWalletsFromTheAPI();          // Get all the wallets from the API

                removeEmptyWalletsFromTheDatabase(balanceList);
                addNewFoundWalletsToTheDatabase(balanceList);

                analyzeIfWalletIsExpired();
                updateInternalValuesOfTheWallets(); // Update the prizes of the wallets
                analyzeIfWalletsCanBeSold();        // Analyze if there is a wallet that must be selled
                buyWalletsIfThereIsMoneyToBuy();    // Analyze if we must buy something

                rq.printWallets(rq.getAllWalletsFromTheDatabase());
            } catch (EngineExeption e) {
                log.warn(e.getMessage());
            }
        }
    }

    private void addNewFoundWalletsToTheDatabase(List<Balance> balanceList) throws EngineExeption {
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        List<String> currentCoinIdInTheDtabase = new LinkedList<>();
        walletList.forEach(wallet -> currentCoinIdInTheDtabase.add(wallet.getCoinId()));    // Make a list of only the coinIds
        for (Balance balance : balanceList) {
            if (currentCoinIdInTheDtabase.contains(balance.getSymbol()) || balance.getAvailable() == 0)
                continue;    // If the wallet is already there, nothing to do

            // From here we have a new wallet Puts
            Wallet newWallet = new Wallet();                        // Create the new wallet
            newWallet.setCoinId(balance.getSymbol());
            newWallet.setMarket(balance.getSymbol() + "-EUR");
            newWallet.setLoss_trigger(Globals.lowlimit);
            newWallet.setAmount(balance.getAvailable());
            newWallet.setPaid(rq.getPricePaidFor(balance.getSymbol()));
            newWallet.setProfit_trigger(Globals.profit_trigger);
            newWallet.setCurrent_price(rq.getPriceOfCoin(balance.getSymbol()));
            newWallet.setCurrent_value(newWallet.getAmount() * newWallet.getCurrent_price());
            newWallet.setEpoch(rq.getBuyMoment(balance.getSymbol()));
            if (newWallet.getHighest_value() < newWallet.getCurrent_value())
                newWallet.setHighest_value(newWallet.getCurrent_value());
            rq.addWallet(newWallet);
            db.addLog("Add new wallet " + newWallet.getCoinId() + " to the database");
        }
    }

    private void removeEmptyWalletsFromTheDatabase(List<Balance> balanceList)  {
        for (Balance balance : balanceList) {
            if (balance.getAvailable() == 0) {                          // Delete wallet for the DB is this one is empty
                rq.deleteWallet(balance.getSymbol());
            }
        }
    }

    private void analyzeIfWalletIsExpired() {
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        long expire = Instant.now().toEpochMilli() - (24 * 3600 * 1000 * Globals.profitExpirationTime);
        for (Wallet wallet : walletList) {
            if (wallet.isExpired() || expire < wallet.getEpoch()) continue;
            double newProfitLimit = 1 + Globals.fee;
            db.addLog(wallet.getCoinId() + " is expired. Set the profitlimit on " + newProfitLimit);
            wallet.setProfit_trigger(newProfitLimit);
            wallet.setExpired(true);
            rq.updateWallet(wallet);
        }
    }

    private void analyzeIfWalletsCanBeSold() throws EngineExeption {
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        for (Wallet wallet : walletList) {
            boolean sellThisWallet = analyzeIfWalletMadeProfit(wallet);
            boolean walletIsInSpike = analyzeIfWalletIsInASpike(wallet);
            if (sellThisWallet || walletIsInSpike || wallet.getSellNow()) {
                db.addLog("Sell " + wallet.getMarket() + " amount of coins is " + df.format(wallet.getAmount()));
                if (Globals.demo) continue;               // demo modus
                sellWallet(wallet);
            }
        }
    }

    private boolean analyzeIfWalletMadeProfit(Wallet wallet) throws EngineExeption {
        if (wallet.getCurrent_value() == 0) return false;
        double profiteLimit = wallet.getPaid() * wallet.getProfit_trigger();
        double currentPrice = rq.getPriceOfCoin(wallet.getCoinId());
        double currentValue = currentPrice * wallet.getAmount();
        double dropTrigger = wallet.getHighest_value() * wallet.getLoss_trigger();

        if (dropTrigger < profiteLimit) return false;
        if (currentValue < profiteLimit) return false;
        if (currentValue > dropTrigger) return false;

        log.debug(wallet.getCoinId() + " Wallet is with " + currentValue + " € is dropped below the drop_trigger of " + dropTrigger + " €, we are going to sell");
        db.addLog("Advise to sell " + wallet.getCoinId());
        return true;
    }

    private boolean analyzeIfWalletIsInASpike(Wallet wallet) {
        double trigger = wallet.getPaid() * Globals.spike_trigger;
        if (trigger > wallet.getCurrent_value()) return false;
        db.addLog("Spike detected, sell " + wallet.getCoinId() + " immediatly");
        return true;
    }

    private void sellWallet(Wallet wallet) throws EngineExeption {
        Order order = rq.executeSellOrder(wallet.getMarket(), wallet.getAmount());
        double profit = order.getFilledAmountQuote() - wallet.getPaid();
        db.addLog("Sold " + wallet.getMarket() + " for " + df.format(order.getFilledAmountQuote()) + "€, profit is " + df.format(profit) + "€");
        rq.deleteWallet(wallet.getCoinId());
    }

    private List<Wallet> updateInternalValuesOfTheWallets() throws EngineExeption {
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        log.debug("update wallets");
        if (Globals.demo) return walletList;

        // Update the values of the wallets
        for (Wallet wallet : walletList) {
            double currentPrice = rq.getPriceOfCoin(wallet.getCoinId());    // Get the coind price
            wallet.setCurrent_value(currentPrice * wallet.getAmount());     // Then calculate its new current value
            wallet.setCurrent_price(currentPrice);                          // Update with the latest price
            // Set the higest value
            if (wallet.getHighest_value() < wallet.getCurrent_value())
                wallet.setHighest_value(wallet.getCurrent_value());
            // If the price drop below the profit price then the highest price is the profit price to reset the price
            double newProfiteLimit = wallet.getPaid() * wallet.getProfit_trigger();     // Calculate the drop limit
            if (wallet.getCurrent_value() < newProfiteLimit)
                wallet.setHighest_value(newProfiteLimit);  // Corrlate the droplimit to the hghest value
            rq.updateWallet(wallet);                        // Update the wallet
        }

        //return the actual wallet state
        return rq.getAllWalletsFromTheDatabase();
    }

    private void buyWalletsIfThereIsMoneyToBuy() throws EngineExeption {
        log.debug("Test if we can buy something");
        List<Wallet> currentWalletList = rq.getAllWalletsFromTheDatabase();
        if (currentWalletList.size() >= Globals.maxAmountOfwallets) {
            log.debug("we hit the maximum of " + Globals.maxAmountOfwallets + " wallets");
            return;
        }
        Double cash = rq.getAmountOfEuros();
        if (cash < Globals.default_paid) return;        // Less then minimal price, to bad. We buy nothing
        if(currentWalletList.size() < Globals.maxAmountOfwallets) cash = Globals.default_paid;  // Only after Globals.maxAmountOfwallets we are going to pay more

        List<String> coinsThatWeAlreadyhave = new ArrayList<>();    // Make a list of coins that we already posses
        currentWalletList.forEach(wallet -> coinsThatWeAlreadyhave.add(wallet.getCoinId()));

        List<String> advisedCoinList = getListOfRisingCoins(); // Get list on interesting coins
        for (String coindId : advisedCoinList) {                // loop trough the coins until we bought one
            if (coinsThatWeAlreadyhave.contains(coindId)) continue;                       // demo modus

            // Test if we are not under the coin limit
            double buyLimit = rq.getCoinLimit(coindId);
            double price = rq.getPriceOfCoin(coindId);
            double numberOfCoinsToBuy = cash / price;           // We are going to buy coins with that what we have
            double priceMinFee = cash * (1 - Globals.fee);      // Calculate how much we can spend when the fee is substracted
            double amount = priceMinFee / price;                // And calculate the number of coins we can buy then

            if (numberOfCoinsToBuy < buyLimit) continue;        // There is a minimal amount of coins that we can buy

            log.debug("We are trying to buy " + amount + " of " + coindId);
            Order order = rq.executeBuyOrder(coindId, amount);      // Execute the order
            if (order.getStatus().equals("ERROR")) {
                log.error("Failed to buy " + coindId + " add it to the blacklist");
                coinsThatWeAlreadyhave.add(coindId);
                continue;
            }
            db.addLog("Bought " + coindId + " for " + df.format(order.getFilledAmountQuote()) + "€");
            Wallet wallet = new Wallet(coindId,
                    coindId + "-EUR",
                    order.getFilledAmountQuote(),
                    Globals.fee,
                    Globals.profit_trigger,
                    Globals.lowlimit,
                    order.getFilledAmount());
            wallet.setEpoch(Instant.now().toEpochMilli());
            rq.addWallet(wallet);

            break;  // If we are here the we bought a coin an then we can break the for loop
        }
    }


    private void wait(int seconds) {
        try {
            log.debug(("Sleep for " + seconds + " seconds"));
            for (int tel = 0; tel < seconds; tel = tel + 5) {
                log.debug("wait " + tel);
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Queries to find the best coin
    public List<String> getListOfRisingCoins() throws EngineExeption {
        List<TickerPrice> coinList = rq.getCurrentListOfCoinValues();       // get a list of all the coins
        Table<Double> coindTable = new Table<>();                           // declare the list of coins we can buy

        for (TickerPrice tickerPrice : coinList) {
            String coinId = rq.marketToCoindId(tickerPrice.getMarket());    // extract the bare name zonder EUR
            List<Candle> candleList = rq.getListOfCandlesPeriod(coinId, 30);    // Get the coin stats over 30 days
            CandleAnalyzer ca = new CandleAnalyzer();           // Declare an analuzer
            ca.addCandles(candleList);                          // Add thee candle list
            ca.analyzeCandles();                                // Analyze the candles
            if (ca.getOverallSlope() > 0.02 && ca.getWeekSlope() > 0.01) {
                coindTable.create(coinId, ca.getOverallSlope() * ca.getWeekSlope());  // test if they are rising, then add the coin to the list
            }
        }
        log.debug("There are " + coindTable.size() + " coins that are adviced to buy");

        List<String> returnList = new LinkedList<>();
        coindTable.readAllSortedAsc().forEach((id, factor) -> returnList.add(id));   // Create the sorted list of keys
        return returnList;
    }

}
