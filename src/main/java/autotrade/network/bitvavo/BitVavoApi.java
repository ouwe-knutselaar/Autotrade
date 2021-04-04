package autotrade.network.bitvavo;

import autotrade.engine.reactive.EngineExeption;
import autotrade.pojo.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

public class BitVavoApi implements TradeApiInterFace {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    Client restApiClient = Client.create();
    Gson gson = new Gson();
    String apiUrl = "https://api.bitvavo.com";

    String apiKey = Globals.apikey;
    String apiSecret = Globals.apisecret;

    public BitVavoApi(){
        if(Globals.debug)log.setLevel(Level.DEBUG);
    }

    @Override
    public List<TickerPrice> getCurrentListOfCoinValues() throws EngineExeption {
        ClientResponse httpResponse = httpGet("/v2/ticker/price", "");
        TickerPrice[] returnlist = gson.fromJson(httpResponse.getEntity(String.class), TickerPrice[].class);
        List<TickerPrice> workList=new LinkedList<>();
        for(TickerPrice tickerPrice : returnlist)workList.add(tickerPrice);
        return workList;
    }

    @Override
    public double getPriceOfCoin(String coinId) throws EngineExeption {
        ClientResponse httpResponse = httpGet("/v2/ticker/price?market=" + coinId + "-EUR", "");
        String resultJson = httpResponse.getEntity(String.class);
        TickerPrice returnlist = gson.fromJson(resultJson, TickerPrice.class);
        return returnlist.getPrice();
    }

    @Override
    public List<Balance> getWallets() throws EngineExeption {
        ClientResponse httpResponse = httpGet("/v2/balance", "");
        Balance[] returnList = gson.fromJson(httpResponse.getEntity(String.class), Balance[].class);
        List<Balance> workList = new LinkedList<>();
        Arrays.asList(returnList).forEach(balance -> workList.add(balance));
        return workList;
    }

    @Override
    public Order executeSellOrder(String market, double amount) throws EngineExeption {
        if(Globals.demo)return new Order();
        long timeStamp = System.currentTimeMillis();

        Map<String, String> body = new HashMap<>();
        body.put("market", market);
        body.put("side", "sell");
        body.put("orderType", "market");
        body.put("amount", "" + amount);
        String stringBody = gson.toJson(body);
        log.debug("Body:"+stringBody);

        String signature = createSignature(timeStamp, "POST", "/v2/order", stringBody);
        try {
            WebResource apiCallUrl = restApiClient.resource(apiUrl + "/v2/order");
            log.debug("call is "+ apiCallUrl.getURI().toString() );
            WebResource.Builder builder = apiCallUrl.accept(MediaType.APPLICATION_JSON);
            builder.header("BITVAVO-ACCESS-KEY", apiKey);
            builder.header("BITVAVO-ACCESS-SIGNATURE", signature);
            builder.header("BITVAVO-ACCESS-TIMESTAMP", "" + timeStamp);
            ClientResponse response = builder.accept("application/json").type("application/json").post(ClientResponse.class, stringBody);
            if (response.getStatus() != 200) {
                logError(response);
                throw new EngineExeption("Cannot sell execute order");
            }
            return gson.fromJson(response.getEntity(String.class),Order.class);
        } catch (ClientHandlerException e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new EngineExeption("Error executing sell order " + e.getMessage());
        }
    }

    @Override
    public Order executeBuyOrder(String coinId, double amountInEur) throws EngineExeption {
        if(Globals.demo)return new Order();
        long timeStamp = System.currentTimeMillis();
        WebResource apiCallUrl = restApiClient.resource(apiUrl + "/v2/order");

        // round amountInEur to two digits
        amountInEur = amountInEur * 100;
        amountInEur = (int) amountInEur;
        amountInEur = amountInEur / 100;

        // Build the body
        String stringBody = "{\"market\":\"" + coinId + "-EUR\",\"orderType\":\"market\",\"side\":\"buy\",\"amount\":" + amountInEur + "}";
        String signature = createSignature(timeStamp, "POST", "/v2/order", stringBody);

        try{
            WebResource.Builder builder = apiCallUrl.accept(MediaType.APPLICATION_JSON);
            builder.header("BITVAVO-ACCESS-KEY", apiKey);
            builder.header("BITVAVO-ACCESS-SIGNATURE", signature);
            builder.header("BITVAVO-ACCESS-TIMESTAMP", "" + timeStamp);
            ClientResponse response = builder.accept("application/json").type("application/json").post(ClientResponse.class, stringBody);
            if (response.getStatus() != 200) {
                logError(response);
                Order failedOrder = new Order();
                failedOrder.setStatus("ERROR");
                return failedOrder;
            }
            return gson.fromJson(response.getEntity(String.class),Order.class);
        } catch (ClientHandlerException e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new EngineExeption("Error " + e.getMessage());
        }
    }

    @Override
    public double getAmountOfEuros() throws EngineExeption {
        ClientResponse result = httpGet("/v2/balance?symbol=EUR", "");
        String strResult = result.getEntity(String.class);
        Balance[] balance = gson.fromJson(strResult, Balance[].class);
        return balance[0].getAvailable();
    }

    @Override
    public List<Trade> getListOftradesFor(String coinId) throws EngineExeption {
        ClientResponse httpResponse = httpGet("/v2/trades?market=" + coinId + "-EUR", "");
        String response = httpResponse.getEntity(String.class);
        Trade[] returnlist = gson.fromJson(response, Trade[].class);
        return Arrays.asList(returnlist);
    }

    @Override
    public List<Candle> getListOfCandlesPeriod(String coinId, long days) throws EngineExeption {
        long epoch = Instant.now().toEpochMilli();
        long startTime = (epoch - (days *24*3600*1000));
        ClientResponse httpResponse = httpGet("/v2/" + coinId + "-EUR/candles?interval=1d&start="+startTime, "");
        String response = httpResponse.getEntity(String.class);
        JsonArray ja = gson.fromJson(response,JsonArray.class);
        Iterator<JsonElement> tempList = ja.iterator();
        List<Candle> candleList = new LinkedList<>();
        while(tempList.hasNext()){
            JsonArray intja = tempList.next().getAsJsonArray();
            candleList.add(new Candle(intja.get(0).getAsLong(),intja.get(1).getAsDouble(),intja.get(2).getAsDouble(),intja.get(3).getAsDouble(),intja.get(4).getAsDouble(),"unknown"));
        }
        return candleList;
    }

    @Override
    public Market getMarket(String coinId) throws EngineExeption {
        ClientResponse httpResponse = httpGet("/v2/markets?market="+coinId+"-EUR", "");
        String body = httpResponse.getEntity(String.class);
        log.debug("trades json:"+body);
        return gson.fromJson(body, Market.class);
    }

    private String createSignature(long timestamp, String method, String urlEndpoint, String body) {
        if (this.apiSecret == null || this.apiKey == null) {
            log.error("The API key or secret has not been set. Please pass the key and secret when instantiating the bitvavo object.");
            System.exit(1);
        }
        String result = String.valueOf(timestamp) + method + urlEndpoint + body;
        Mac sha256HMAC;
        String sign = "";
        try {
            sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(this.apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            sign = new String(Hex.encodeHex(sha256HMAC.doFinal(result.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sign;
    }

    private ClientResponse httpGet(String url, String body) throws EngineExeption {
        try {
            String methode = "GET";
            long timeStamp = System.currentTimeMillis();
            String signature = createSignature(timeStamp, methode, url, body);

            WebResource webResource = restApiClient.resource(apiUrl + url);
            WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_JSON);

            builder.header("BITVAVO-ACCESS-KEY", apiKey);
            builder.header("BITVAVO-ACCESS-SIGNATURE", signature);
            builder.header("BITVAVO-ACCESS-TIMESTAMP", "" + timeStamp);

            ClientResponse httpResponse = builder.get(ClientResponse.class);
            if (httpResponse.getStatus() != 200) {
                logError(httpResponse);
                throw new EngineExeption("Error " + httpResponse.getStatus() + " Cannot access user " + httpResponse.getStatusInfo().getReasonPhrase());
            }
            return httpResponse;
        } catch (ClientHandlerException e) {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            throw new EngineExeption("Error " + e.getMessage());
        }
    }

    private void logError(ClientResponse response){
        log.error(response.getStatus());
        log.error(response.getStatusInfo().getReasonPhrase());
        log.error(response.getStatusInfo().getStatusCode());
        MultivaluedMap<String, String> headers = response.getHeaders();
        headers.forEach((header,value) -> log.error("Header: "+header+" "+value));

        Map<String,Object> propertiesList =response.getProperties();
        propertiesList.forEach((header,value) -> log.error("Property: "+header+" "+value));
    }

}
