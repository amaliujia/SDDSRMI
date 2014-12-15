import java.io.IOException;

/**
 * Created by amaliujia on 14-11-24.
 */
public class SDProcessCenter {

    // default port, used as listener monitoring port
    private static final int defaultPort = 16640;


    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            //start master
            SDMasterNode master = new SDMasterNode();
            master.startService(defaultPort);
        }else if(args.length == 2){
            //start salve

        }else{
            System.out.println("Arguments wrong");
        }
    }
}
