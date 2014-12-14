import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Created by amaliujia on 14-12-13.
 */
public class SDSlave {
    private InetAddress address;
    private int port;
    private BufferedReader in;
    private PrintWriter out;

    public SDSlave(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public void setReader(BufferedReader reader){
       this.in = reader;
    }

    public void setWriter(PrintWriter writer){
        this.out = writer;
    }

    public String toString(){
       return "INetAddress:\t" + address + " \tport:\t" + port;
    }
}
