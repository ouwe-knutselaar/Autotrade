package autotrade.pojo;

public class Candle {

    private final double large  = 2.5;
    private final double veryLarge  = 5;
    private final double small = 0.5;

    private long epoch;
    private double start;
    private double end;
    private double low;
    private double high;
    private String candleType;
    private double slope;
    private double average;
    private double candleSize;
    private double fullsize;
    private double topWickSize;
    private double bottomWickSize;
    private boolean isProfit;
    private boolean isLoss;
    private boolean isDoji;
    private boolean isBigbody;
    private boolean hasLargeBottomWick;
    private boolean hasVeryLargeBottomWick;
    private boolean hasLargeTopWick;
    private boolean hasVeryLargeTopWick;
    private boolean hasSmallTopWick;
    private boolean hasSmallBottomWick;

    public Candle(long epoch, double start, double high, double low, double end, String candleType) {
        this.epoch = epoch;
        this.start = start;
        this.end = end;
        this.low = low;
        this.high = high;
        this.candleType = candleType;

        average = (start + end + low + high) / 4;
        slope = end / start;
        candleSize = Math.abs(end - start);
        fullsize = high - low;

        if (start > end) topWickSize = high - start;
        else topWickSize = high - end;

        if (start > end) bottomWickSize = end - low;
        else bottomWickSize = start - low;

        isProfit = end > start;
        isLoss = start > end;
        hasLargeBottomWick = (bottomWickSize / candleSize) > large;
        hasVeryLargeBottomWick = (bottomWickSize / candleSize) > veryLarge;
        hasLargeTopWick = (topWickSize / candleSize)  > large;
        hasVeryLargeTopWick = (topWickSize / candleSize) > veryLarge;
        hasSmallTopWick = (topWickSize / candleSize) < small;
        hasSmallBottomWick = (bottomWickSize / candleSize) < small;
        isDoji = (candleSize / fullsize) < 0.1;
        isBigbody = (candleSize / average) > 0.1;

        this.candleType = getCandleType();
    }


    // functions for combined types
    public boolean isHammer() {
        return (hasSmallTopWick & hasLargeBottomWick & isProfit);
    }

    public boolean isInvertedHammer() {
        return (hasLargeTopWick & hasSmallBottomWick & isProfit);
    }

    public boolean isLongUpperShadow() {
        return (hasLargeTopWick & hasSmallBottomWick & isLoss);
    }

    public boolean isHangingMan() {
        return (hasLargeBottomWick & isLoss);
    }

    public boolean isDragonFlyDoji() {
        return (isDoji & hasVeryLargeBottomWick & hasSmallTopWick);
    }

    public boolean isGravestoeDoji() {
        return isDoji & hasSmallBottomWick & hasVeryLargeTopWick;
    }

    public boolean isLongLeggedDoji() {
        return isDoji & hasLargeBottomWick & hasLargeTopWick;
    }

    public boolean isDoji() {
        return isDoji;
    }

    public boolean isBigWhiteCandle() {
        return (isBigbody & hasSmallTopWick & hasSmallBottomWick);
    }

    // getters
    public String getCandleType() {

        if(isInvertedHammer())return "inverted hammer";
        if(isLongUpperShadow())return "lomg upper shadow";
        if(isBigWhiteCandle())return "big white candle";
        if(isHammer())return "hammer";
        if(isHangingMan())return "hanging man";
        if(isDragonFlyDoji())return "dragon fly doji";
        if(isGravestoeDoji())return "gravestone doji";
        if(isDoji())return "doji";

        return "unknown";
    }

    public long getEpoch() {
        return epoch;
    }

    public double getSlope() {
        return slope;
    }

    public double getAverage() {
        return average;
    }

    @Override
    public String toString() {
        return "Candle{" +
                ", epoch=" + epoch +
                ", isProfit=" + isProfit +
                ", isLoss=" + isLoss +
                ", isDoji=" + isDoji +
                ", isBigbody=" + isBigbody +
                ", hasLargeBottomWick=" + hasLargeBottomWick +
                ", hasVeryLargeBottomWick=" + hasVeryLargeBottomWick +
                ", hasLargeTopWick=" + hasLargeTopWick +
                ", hasVeryLargeTopWick=" + hasVeryLargeTopWick +
                ", hasSmallTopWick=" + hasSmallTopWick +
                ", hasSmallBottomWick=" + hasSmallBottomWick +
                ", candleType='" + candleType +
                '}';
    }
}
