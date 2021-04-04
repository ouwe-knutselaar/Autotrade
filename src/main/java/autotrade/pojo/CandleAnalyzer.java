package autotrade.pojo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class CandleAnalyzer {

    List<Candle> workList = new LinkedList<Candle>();
    List<Long> tripleDownMoments = new LinkedList<>();
    double overallAverage;
    double overallSlope = 0;
    double weekSlope=0;
    double overallIntercept=0;
    boolean isThreeLineStrike = false;
    boolean isThreeWhiteSoldiers = false;

    public void addCandles(List<Candle> newCandleList) {
        workList.addAll(newCandleList);
    }

    public List<Candle> analyzeCandles() {

        // process candle list
        LinkedList<Candle> returnList = new LinkedList<>();
        for (Candle candle : workList) {
            //candle.setCandleType(analyze(candle));
            returnList.addLast(candle);
        }
        if(returnList.size() == 0 )return returnList;

        // calculate slope
        double sum = 0;
        for (Candle candle : returnList) sum = sum + candle.getAverage();
        overallAverage = (sum / returnList.size());

        SimpleRegression sr = new SimpleRegression();
        int tel=0;
        for (Candle candle : returnList) {
            sr.addData(tel, candle.getAverage());
            tel++;
        }
        overallSlope = sr.getSlope()*-1;

        sr.clear();
        for(tel=0;tel<7;tel++){
            sr.addData(tel,returnList.get(tel).getAverage());
        }
        weekSlope = sr.getSlope()*-1;

        // Search the triple down patern
        int numberOfDowns = 0;
        for (Candle candle : returnList) {
            if (candle.getSlope() < 1) numberOfDowns++;
            if (candle.getSlope() > 1) {
                if (numberOfDowns > 2) {
                    tripleDownMoments.add(candle.getEpoch());
                    numberOfDowns = 0;
                } else {
                    numberOfDowns = 0;
                }
            }
        }

        // Detect patterns
        // Three line strike
        if (returnList.get(0).getSlope() < 1 & returnList.get(1).getSlope() < 1 & returnList.get(1).getSlope() < 1)
            isThreeLineStrike = true;
        if (returnList.get(1).getSlope() < 1 & returnList.get(2).getSlope() < 1 & returnList.get(3).getSlope() < 1)
            isThreeLineStrike = true;

        // Three white soldiers
        if (returnList.get(0).getSlope() > 1 & returnList.get(1).getSlope() > 1 & returnList.get(1).getSlope() > 1)
            isThreeWhiteSoldiers = true;
        if (returnList.get(1).getSlope() > 1 & returnList.get(2).getSlope() > 1 & returnList.get(3).getSlope() > 1)
            isThreeWhiteSoldiers = true;

        returnList.stream().findFirst().get();
        returnList.get(0);

        return returnList;
    }

    public double getOverallSlope() {
        return overallSlope;
    }

    public double getOverallAverage() {
        return overallAverage;
    }

    public double getWeekSlope(){
        return weekSlope;
    }

/*
    |           wick_top
    |
    |               top_wick_size
    |
 +-----+        body_top
 +     +
 +     +
 +-----+        body_bottom
    |
    |               bottom_wick_size
    |
    |           wick_bottom

----------      zero_line

 */


    @Override
    public String toString() {
        return "CandleAnalyzer{" +
                "tripleDownMoments=" + tripleDownMoments +
                ", overallAverage=" + overallAverage +
                ", overallSlope=" + overallSlope +
                ", weekSlope=" + weekSlope +
                ", overallIntercept=" + overallIntercept +
                ", isTreeLineStrike=" + isThreeLineStrike +
                ", isThreeWhiteSoldiers=" + isThreeWhiteSoldiers +
                '}';
    }
}
