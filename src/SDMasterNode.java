import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by amaliujia on 14-11-24.
 */
public class SDMasterNode {

    public void startService(){
        ListenerService listener = new ListenerService(16440);
        listener.start();
    }

    public void startService(int port){
        ListenerService listener = new ListenerService(port);
        listener.start();
    }


    private class ListenerService extends Thread{
        ServerSocket listener = null;

        public ListenerService(int port){
            try {
                listener = new ServerSocket(port);
            } catch (IOException e) {
                System.out.println("listener socket fails");
                e.printStackTrace();
            }
        }

        public void run() {
            while(true) {
                try {
                    System.out.println("11111");
                    Socket sock = listener.accept();
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                }catch (IOException e){
                    System.err.println("fail to establish a socket with a slave node");
                    e.printStackTrace();
                }
            }
        }

    }

}
