package autotrade.pojo;

public class Order {

    private String orderId;
    private String market;
    private String created;
    private String updated;
    private String status;
    private String side;
    private String orderType;
    private double amount;
    private double amountRemaining;
    private double price;
    private double amountQuote;
    private double amountQuoteRemaining;
    private String onHold;
    private String onHoldCurrency;
    private double triggerPrice;
    private double triggerAmount;
    private String triggerType;
    private String triggerReference;
    private double filledAmount;
    private double filledAmountQuote;
    private double feePaid;
    private String feeCurrency;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(double amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmountQuote() {
        return amountQuote;
    }

    public void setAmountQuote(double amountQuote) {
        this.amountQuote = amountQuote;
    }

    public double getAmountQuoteRemaining() {
        return amountQuoteRemaining;
    }

    public void setAmountQuoteRemaining(double amountQuoteRemaining) {
        this.amountQuoteRemaining = amountQuoteRemaining;
    }

    public String getOnHold() {
        return onHold;
    }

    public void setOnHold(String onHold) {
        this.onHold = onHold;
    }

    public String getOnHoldCurrency() {
        return onHoldCurrency;
    }

    public void setOnHoldCurrency(String onHoldCurrency) {
        this.onHoldCurrency = onHoldCurrency;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public double getTriggerAmount() {
        return triggerAmount;
    }

    public void setTriggerAmount(double triggerAmount) {
        this.triggerAmount = triggerAmount;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerReference() {
        return triggerReference;
    }

    public void setTriggerReference(String triggerReference) {
        this.triggerReference = triggerReference;
    }

    public double getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(double filledAmount) {
        this.filledAmount = filledAmount;
    }

    public double getFilledAmountQuote() {
        return filledAmountQuote;
    }

    public void setFilledAmountQuote(double filledAmountQuote) {
        this.filledAmountQuote = filledAmountQuote;
    }

    public double getFeePaid() {
        return feePaid;
    }

    public void setFeePaid(double feePaid) {
        this.feePaid = feePaid;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", market='" + market + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", status='" + status + '\'' +
                ", side='" + side + '\'' +
                ", orderType='" + orderType + '\'' +
                ", amount='" + amount + '\'' +
                ", amountRemaining='" + amountRemaining + '\'' +
                ", price='" + price + '\'' +
                ", amountQuote='" + amountQuote + '\'' +
                ", amountQuoteRemaining='" + amountQuoteRemaining + '\'' +
                ", onHold='" + onHold + '\'' +
                ", onHoldCurrency='" + onHoldCurrency + '\'' +
                ", triggerPrice='" + triggerPrice + '\'' +
                ", triggerAmount='" + triggerAmount + '\'' +
                ", triggerType='" + triggerType + '\'' +
                ", triggerReference='" + triggerReference + '\'' +
                ", filledAmount='" + filledAmount + '\'' +
                ", filledAmountQuote='" + filledAmountQuote + '\'' +
                ", feePaid='" + feePaid + '\'' +
                ", feeCurrency='" + feeCurrency + '\'' +
                '}';
    }
}
