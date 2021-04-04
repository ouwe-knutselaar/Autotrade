package autotrade.pojo;

import java.io.Serializable;

public class Wallet implements Serializable {

    private String coinId;
    private String market;
    private double startValue=0;
    private double current_value;
    private double paid;
    private double paid_fee;
    private double price_at_buy;
    private double profit_trigger;
    private double loss_trigger;
    private double highest_value;
    private double current_price;
    private Boolean sellNow=false;
    private boolean expired = false;
    private double amount;
    private long epoch;

    public Wallet()
    {

    }

    public Wallet(String coinId, String market, double paid, double paid_fee, double profit_trigger, double loss_trigger, double amount) {
        this.coinId = coinId;
        this.market = market;
        this.paid = paid;
        this.paid_fee = paid_fee;
        this.profit_trigger = profit_trigger;
        this.loss_trigger = loss_trigger;
        this.amount = amount;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getPaid_fee() {
        return paid_fee;
    }

    public void setPaid_fee(double paid_fee) {
        this.paid_fee = paid_fee;
    }

    public double getPrice_at_buy() {
        return price_at_buy;
    }

    public void setPrice_at_buy(double price_at_buy) {
        this.price_at_buy = price_at_buy;
    }

    public double getProfit_trigger() {
        return profit_trigger;
    }

    public void setProfit_trigger(double profit_trigger) {
        this.profit_trigger = profit_trigger;
    }

    public double getLoss_trigger() {
        return loss_trigger;
    }

    public void setLoss_trigger(double loss_trigger) {
        this.loss_trigger = loss_trigger;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public double getStartValue() {
        return startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    public double getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(double current_value) {
        this.current_value = current_value;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getHighest_value() {
        return highest_value;
    }

    public void setHighest_value(double newMaxvalue) {
        if(newMaxvalue> highest_value) highest_value = newMaxvalue;
    }

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public Boolean getSellNow() {
        return sellNow;
    }

    public void setSellNow(Boolean sellNow) {
        this.sellNow = sellNow;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "coinId='" + coinId + '\'' +
                ", market='" + market + '\'' +
                ", startValue=" + startValue +
                ", currentValue=" + current_value +
                ", paid=" + paid +
                ", paid_fee=" + paid_fee +
                ", price_at_buy=" + price_at_buy +
                ", profit_trigger=" + profit_trigger +
                ", loss_trigger=" + loss_trigger +
                ", highest_value=" + highest_value +
                ", current_price=" + current_price +
                ", sellNow=" + sellNow +
                ", expired=" + expired +
                ", amount=" + amount +
                '}';
    }


    public String info() {
        String result = String.format("coinId %s\tcurrent_value=%.5f\thighest_value=%.5f\tcurrent_price=%.5f",coinId,current_value,highest_value,current_price);
        return result;
    }
}
