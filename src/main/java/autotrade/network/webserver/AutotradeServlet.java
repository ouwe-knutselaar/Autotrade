package autotrade.network.webserver;

import autotrade.database.InFileDB;
import autotrade.engine.reactive.ReactiveQueries;
import autotrade.pojo.Globals;
import autotrade.pojo.Wallet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AutotradeServlet extends HttpServlet {

    private final ReactiveQueries rq = ReactiveQueries.getInstance();
    private final InFileDB db = InFileDB.getInstance();
    private final DecimalFormat df = new DecimalFormat("#.####");

    private String webPage = "<html><head>" +
            "<title>Autotrade</title>" +
            "<meta http-equiv=\"refresh\" content=\""+Globals.interval+"\"> " +
            "</head><body><code>" +
            "{{ HEADER }}<br>" +
            "{{ COINTABLE }}<br>" +
            //"{{ LEDGER }}<br>" +
            "{{ LOGGING }}" +
            "{{ FOOTER }}" +
            "</code></BODY></HTML>";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        String result = new String(webPage);

        result = result.replace("{{ HEADER }}", makeHeader());
        result = result.replace("{{ COINTABLE }}", makeCoinTable());
        result = result.replace("{{ LOGGING }}", makeLogging());
        result = result.replace("{{ FOOTER }}", makeFooter());

        response.getWriter().println(result);
    }


    private String makeHeader() {
        Date date = java.util.Calendar.getInstance().getTime();
        return date.toString();
    }

    private String makeCoinTable() {
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        double total = 0;
        StringBuilder sb = new StringBuilder("<table border=1>");
        sb.append("<tr><td>Coint ID</td><td>+/-</td><td>current price</td><td>profit limit</td><td>highest value</td><td>drop limit</td><td>paid</td><td>buy date</td></tr>");
        List<Wallet> walletList = rq.getAllWalletsFromTheDatabase();
        for (Wallet wallet : walletList) {
            double profit = wallet.getPaid() * wallet.getProfit_trigger();
            sb.append("<tr>");
            sb.append("<td>").append(wallet.getCoinId()).append("</td>");

            if(wallet.getCurrent_value()>profit)sb.append("<td style=\"background-color:#00FF00\">+");
            else sb.append("<td>");
            sb.append("</td>");

            sb.append("<td>").append(df.format(wallet.getCurrent_value())).append("</td>");
            sb.append("<td>").append(df.format(wallet.getPaid() * wallet.getProfit_trigger())).append("</td>");
            sb.append("<td>").append(df.format(wallet.getHighest_value())).append("</td>");
            sb.append("<td>").append(df.format(wallet.getLoss_trigger() * wallet.getHighest_value())).append("</td>");
            sb.append("<td>").append(df.format(wallet.getPaid())).append("</td>");
            sb.append("<td>").append(jdf.format(new Date(wallet.getEpoch()))).append("</td>");
            sb.append("</tr>");
            total += wallet.getCurrent_value();
        }
        double remains = rq.getAmountOfEuros();
        sb.append("<tr>");
        sb.append("<td></td><td>").append("EUR").append("</td>");
        sb.append("<td>").append(df.format(remains)).append("</td>");
        sb.append("<td></td><td></td><td></td><td></td><td></td>");
        sb.append("</tr>");

        total += remains;
        sb.append("<tr>");
        sb.append("<td></td><td>").append("Total").append("</td>");
        sb.append("<td>").append(df.format(total)).append("</td>");
        sb.append("<td></td><td></td><td></td><td></td><td></td>");
        sb.append("</tr>");
        sb.append("</table>");

        return sb.toString();
    }

    private String makeLogging() {
        List<String> workList = db.getNumOfLogRecords(20);
        StringBuilder sb = new StringBuilder("<br>LOGGING<br>");
        workList.forEach(line -> sb.append(line).append("<br>"));
        return sb.toString();
    }

    private CharSequence makeFooter() {
        StringBuilder sb = new StringBuilder("<table>");
        sb.append("<br><tr><td>Profit limit </td><td>").append(Globals.profit_trigger).append("</td></tr>");
        sb.append("<tr><td>Low limit </td><td>").append(Globals.lowlimit).append("</td></tr>");
        sb.append("<tr><td>Spike trigger </td><td>").append(Globals.spike_trigger).append("</td></tr>");
        sb.append("<tr><td>Default price </td><td>").append(Globals.default_paid).append("</td></tr>");
        sb.append("<tr><td>Maximum amount of wallets</td><td>").append(Globals.maxAmountOfwallets).append("</td></tr>");
        sb.append("<tr><td>Expiration in hours </td><td>").append(Globals.profitExpirationTime).append("</td></tr>");
        sb.append("<tr><td>Refresh rate in seconds</td><td>").append(Globals.interval).append("</td></tr>");
        sb.append("</table>");

        return sb.toString();
    }

}
