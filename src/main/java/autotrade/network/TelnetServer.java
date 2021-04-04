package autotrade.network;

import autotrade.engine.reactive.EngineExeption;
import autotrade.engine.reactive.ReactiveQueries;
import autotrade.pojo.Globals;
import autotrade.pojo.Wallet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TelnetServer implements Runnable{

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    private int port = 3030;
    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(256);
    private ByteBuffer welcomeBuf = ByteBuffer.wrap("Autotrade 1.0 console\n\r".getBytes());
    private ReactiveQueries engine;
    private List<String> history = new LinkedList<>();

    public TelnetServer() throws IOException {

        if(Globals.debug)log.setLevel(Level.DEBUG);
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        log.info("Telnet server started at "+port);
        engine =ReactiveQueries.getInstance();

    }

    public void loop() throws IOException {
        log.info("Start telnet server");

        Iterator<SelectionKey> iter;
        SelectionKey skey;
        while(Globals.telnetThread)
        {
            selector.select();
            iter=selector.selectedKeys().iterator();
            while(iter.hasNext())
            {
                skey=iter.next();
                iter.remove();
                try{
                if(skey.isAcceptable())handleAccept(skey);
                if(skey.isReadable())handleRead(skey);
                }catch(ClosedChannelException e){
                    log.debug("Channel externally closed");
                }
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {

        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        buf.clear();
        int read = 0;
        while( (read = ch.read(buf)) > 0 ) {
            buf.flip();
            byte[] bytes = new byte[buf.limit()];
            buf.get(bytes);
            sb.append(new String(bytes));
            buf.clear();
        }
        String msg;
        if(read<0) {
            msg = key.attachment()+" left the chat.\n";
            ch.close();
        }
        else {
            String answer = processInput(sb.toString());
            history.add(sb.toString());
            msg = key.attachment()+": "+answer+"\n\r";
        }
        ByteBuffer msgBuf=ByteBuffer.wrap(msg.getBytes());
        ch.write(msgBuf);
    }

    private void handleAccept(SelectionKey skey) throws IOException {
        SocketChannel sc = ((ServerSocketChannel)skey.channel()).accept();
        String address = (new StringBuilder( sc.socket().getInetAddress().toString() )).append(":").append( sc.socket().getPort() ).toString();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ, ">");

        StringBuilder sb = new StringBuilder();
        buf.clear();
        int read = 0;
        while( (read = sc.read(buf)) > 0 ) {
            buf.flip();
            byte[] bytes = new byte[buf.limit()];
            buf.get(bytes);
            sb.append(new String(bytes));
            buf.clear();
        }
        log.debug("accp resp"+ sb.toString());

        sc.write(welcomeBuf);
        welcomeBuf.rewind();
        log.debug("accepted connection from: "+address);
    }

    @Override
    public void run() {
        try {
            loop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processInput(String response) {
        log.debug("response "+response);
        String[] parameters = response.split("\\s+");
        switch(parameters[0]) {
            case "help":
                return showHelp();
            case "show":
                return Show(parameters);
            case "alter":
                if (parameters.length == 1) return "ERROR";
                return alterParameter(parameters);
            case "sell":
                return sellCoin(parameters);
            default:
                return "Unknow command";
        }
    }

    private String alterParameter(String[] parameters) {
        switch(parameters[0]){
            case  "profit_limit":
                return "update profit limit";
            default:
                return "error in alter command";
        }
    }

    private String showHelp(){
        StringBuilder answer = new StringBuilder("Help info").append(System.lineSeparator()).
                append("status                                     show status").append(System.lineSeparator()).
                append("show wallets                               show all the wallets").append(System.lineSeparator()).
                append("show blacklist                             show all the wallets").append(System.lineSeparator()).
                append("alter profit_limit [COINID] [new value]    alter the profit limit").append(System.lineSeparator()).
                append("sell [COINID]                              sell a coin").append(System.lineSeparator()).
                append("buy [COINID]                               buy a coin").append(System.lineSeparator()).
                append("add blacklist [COINDID]                    add coin to the blacklist").append(System.lineSeparator()).
                append("remove blacklist [COINID]                  remove coin from the blacklist").append(System.lineSeparator()).
                append("stop selling                               stop selling").append(System.lineSeparator()).
                append("stop autotrade                             stop the software").append(System.lineSeparator()).
                append("quit                                       exit session").append(System.lineSeparator());
        return answer.toString();
    }

    private String Show(String[] parameters)    {
        if(parameters.length==1)return "Error in SHOW, missing parameters";
        if(parameters[1].equals("wallets"))return showWallets();
        if(parameters[1].equals("blacklist"))return showBlackList();
        return "Unknown command for show";
    }

    private String sellCoin(String[] parameters){
        if(parameters.length!=2)return "Error in SELL, parameters error";
        List<Wallet>walletList = engine.getAllWalletsFromTheDatabase();
        for(Wallet wallet : walletList){
            if(wallet.getCoinId().equals(parameters[1])){
                wallet.setSellNow(true);
                engine.updateWallet(wallet);
                return "Coin "+parameters[1]+" will be sold in the next round";
            }
        }
        return "coin "+parameters[1]+" is unknown";
    }

    private String showBlackList()    {
        return "show blacklist";
    }

    private String showWallets()    {
        List<Wallet> workList= engine.getAllWalletsFromTheDatabase();
        List<String> output= engine.printWallets(workList);
        StringBuilder msg=new StringBuilder(System.lineSeparator());
        output.forEach(line -> msg.append(line).append(System.lineSeparator()));
        return msg.toString();
    }


}

