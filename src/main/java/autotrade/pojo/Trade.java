package autotrade.pojo;

public class Trade {

    private String id;
    private String orderId;
    private long timestamp;
    private String market;
    private String side;
    private double amount;
    private double price;
    private String taker;
    private double fee;
    private String feeCurrency;
    private Boolean settled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTaker() {
        return taker;
    }

    public void setTaker(String taker) {
        this.taker = taker;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", market='" + market + '\'' +
                ", side='" + side + '\'' +
                ", amount='" + amount + '\'' +
                ", price='" + price + '\'' +
                ", taker='" + taker + '\'' +
                ", fee='" + fee + '\'' +
                ", feeCurrency='" + feeCurrency + '\'' +
                ", settled=" + settled +
                '}';
    }
}
