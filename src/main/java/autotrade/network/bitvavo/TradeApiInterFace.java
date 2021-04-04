package autotrade.network.bitvavo;

import autotrade.engine.reactive.EngineExeption;
import autotrade.pojo.*;
import java.util.List;

public interface TradeApiInterFace {
    List<TickerPrice> getCurrentListOfCoinValues() throws EngineExeption;

    double getPriceOfCoin(String coinId) throws EngineExeption;

    List<Balance> getWallets() throws EngineExeption;

    Order executeSellOrder(String market, double amount) throws EngineExeption;

    Order executeBuyOrder(String coinId, double amountInEur) throws EngineExeption;

    double getAmountOfEuros() throws EngineExeption;

    List<Trade> getListOftradesFor(String coinId) throws EngineExeption;

    List<Candle> getListOfCandlesPeriod(String coinId, long days) throws EngineExeption;

    Market getMarket(String coinId) throws EngineExeption;
}
