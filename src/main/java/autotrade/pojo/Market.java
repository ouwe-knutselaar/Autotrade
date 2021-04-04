package autotrade.pojo;

public class Market {

    private String market;
    private String status;
    private String base;
    private String quote;
    private int pricePrecision;
    private double minOrderInQuoteAsset;
    private double minOrderInBaseAsset;

    public String getMarket() {
        return market;
    }

    public Market(){
    };

    public Market(String market, String status, String base, String quote, int pricePrecision, double minOrderInQuoteAsset, double minOrderInBaseAsset) {
        this.market = market;
        this.status = status;
        this.base = base;
        this.quote = quote;
        this.pricePrecision = pricePrecision;
        this.minOrderInQuoteAsset = minOrderInQuoteAsset;
        this.minOrderInBaseAsset = minOrderInBaseAsset;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public double getMinOrderInQuoteAsset() {
        return minOrderInQuoteAsset;
    }

    public void setMinOrderInQuoteAsset(double minOrderInQuoteAsset) {
        this.minOrderInQuoteAsset = minOrderInQuoteAsset;
    }

    public double getMinOrderInBaseAsset() {
        return minOrderInBaseAsset;
    }

    public void setMinOrderInBaseAsset(double minOrderInBaseAsset) {
        this.minOrderInBaseAsset = minOrderInBaseAsset;
    }
}
