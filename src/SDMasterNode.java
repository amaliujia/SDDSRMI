import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by amaliujia on 14-11-24.
 */
public class SDMasterNode {

    public static ArrayList<SDSlave> salveList;

    public void startService(){
        ListenerService listener = new ListenerService(16440);
        listener.start();
    }

    public void startService(int port) throws IOException {

        //start monitoring port
        ListenerService listener = new ListenerService(port);
        listener.start();

        //read command
        BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
        promptPrinter("help");

        String inputString;
        String[] args;

        while(true){
            inputString = stdReader.readLine();
            args = inputString.split(" ");

            if(args.length == 0){
                continue;
            }else if(args[0].equals("help")){
                promptPrinter(args[0]);
            }else if(args[0].equals("exit")){
                SDUtil.fatalError("");
            }else if(args[0].equals("ps")){
                //list processes
            }else if(args[0].equals("ls")){
               synchronized (salveList){
                   for(int i = 0; i < salveList.size(); i++){
                       System.out.println(salveList.toString());
                   }
               }
            }
        }
    }

    private void promptPrinter(String message){
        if(message.equals("help")){
            System.out.println("Instruction: Please input your command based on following format");
            System.out.println("              <processName> [arg1] [arg2]....[argN]");
            System.out.println("              ps (prints a list of local running processes and their arguments)");
            System.out.println("              quit (exits the ProcessManager)");
        }

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
                    Socket sock = listener.accept();
                    SDSlave aSlave = new SDSlave(sock.getInetAddress(), sock.getPort());
                    aSlave.setReader(new BufferedReader(new InputStreamReader(sock.getInputStream())));
                    aSlave.setWriter(new PrintWriter(sock.getOutputStream()));
                    synchronized (salveList){
                        salveList.add(aSlave);
                    }
                }catch (IOException e){
                    System.err.println("fail to establish a socket with a slave node");
                    e.printStackTrace();
                }
            }
        }

    }

}
