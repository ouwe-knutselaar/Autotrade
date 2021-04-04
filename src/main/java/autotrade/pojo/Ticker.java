package autotrade.pojo;

public class Ticker {
    private String market;
    private String coinId;
    private String open;
    private String high;
    private String low;
    private String last;
    private String volume;
    private String volumeQuote;
    private String bid;
    private String bidSize;
    private String ask;
    private String askSize;
    private long timestamp;
    private double rise;

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolumeQuote() {
        return volumeQuote;
    }

    public void setVolumeQuote(String volumeQuote) {
        this.volumeQuote = volumeQuote;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBidSize() {
        return bidSize;
    }

    public void setBidSize(String bidSize) {
        this.bidSize = bidSize;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getAskSize() {
        return askSize;
    }

    public void setAskSize(String askSize) {
        this.askSize = askSize;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public double getRise() {
        return rise;
    }

    public void setRise(double rise) {
        this.rise = rise;
    }

    public String toRiseString() {
        return "Ticker{" +
                "coinId='" + coinId + '\'' +
                ", last='" + last + '\'' +
                ", rise=" + rise +
                '}';
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "market='" + market + '\'' +
                ", coinId='" + coinId + '\'' +
                ", open='" + open + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", last='" + last + '\'' +
                ", volume='" + volume + '\'' +
                ", rise=" + rise +
                '}';
    }
}
