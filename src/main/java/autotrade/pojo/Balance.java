package autotrade.pojo;

public class Balance {

    private String symbol;
    private double available;
    private String inOrder;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    public String getInOrder() {
        return inOrder;
    }

    public void setInOrder(String inOrder) {
        this.inOrder = inOrder;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "symbol='" + symbol + '\'' +
                ", available='" + available + '\'' +
                ", inOrder='" + inOrder + '\'' +
                '}';
    }
}
