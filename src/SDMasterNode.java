import javax.rmi.CORBA.Util;
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

    public static ArrayList<SDSlave> slaveList;

    public SDMasterNode(){
        slaveList = new ArrayList<SDSlave>();
    }

    public void startService(){
        ListenerService listener = new ListenerService(16440);
        listener.start();
    }

    /**
     * Start listening service.
     * @param port
     *          local monitoring port.
     * @throws IOException
     *          throw this exception when
     */
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
                synchronized (slaveList){
                    for(int i = 0; i < slaveList.size(); i++){
                        SDSlave slave = slaveList.get(i);
                        PrintWriter out = slave.getWriter();
                        out.println("ps");
                        out.flush();

                        try {
                            String line = slave.in.readLine();
                            if(line != null){
                                System.out.println(line);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }else if(args[0].equals("ls")){
               synchronized (slaveList){
                   for(int i = 0; i < slaveList.size(); i++){
                       System.out.println("ID: " + i + " " + slaveList.get(i).toString());
                   }
               }
            }
            /*
			 * to start a new process running on a specific slave host, the
			 * syntax should be
			 * "start slaveID someProcess inputFile outputFile"
			 */
            else if(args[0].equals("start") && args.length > 1){
                int slaveID = -1;
                try{
                    slaveID = Integer.parseInt(args[1]);
                }catch(Exception e){
                    System.err.println("wrong format of start command");
                }
                if(slaveID > slaveList.size() || slaveID < 0){
                    promptPrinter("start");
                    continue;
                }else{
                    // start slaveID processID
                    SDSlave slave = this.slaveList.get(slaveID);
                    PrintWriter out = slave.getWriter();
                    out.write("start " + SDUtil.inputFilePath[Integer.parseInt(args[2])] + " " +
                                        SDUtil.outputFilePath[Integer.parseInt(args[2])] + "\n");
                    out.flush();
                }

            }else if(args[0].equals("miga")){ //miga slaveID processID slaveID
                if(args.length > 4){
                    promptPrinter("help");
                    continue;
                }
                int ida = Integer.parseInt(args[1]);
                int idb = Integer.parseInt(args[3]);

                SDSlave slave = slaveList.get(ida);
                slave.out.println("suspend " + args[2]);
                slave.out.flush();

                try{
                    String line = slave.in.readLine();
                    if(line == null){
                        SDUtil.fatalError("Greatly disaster");
                    }
                    if(line.equals("ACK")){
                        slave = slaveList.get(idb);
                        slave.out.println("resume " + args[2]);
                        slave.out.flush();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }else if(args[0].equals("test")){
                int i = 0;
                SDSlave slave = slaveList.get(0);
                while(i < 5){
                    slave.out.write(i + "\n");
                    i++;
                }
                slave.out.flush();
            }else{
                promptPrinter("help");
                continue;
            }
        }
    }

    /**
     * Print appropriate messages in console.
     * @param message
     *          a String, used to print different messages into console.
     */
    private void promptPrinter(String message){
        if(message.equals("help")){
            System.out.println("Instruction: Please input your command based on following format");
            System.out.println("              <processName> [arg1] [arg2]....[argN]");
            System.out.println("              ps (prints a list of local running processes and their arguments)");
            System.out.println("              quit (exits the ProcessManager)");
        }else if(message.equals("start")){
            System.out.println("Start Command: ");
            System.out.println("              ---- start slaveID someProcess inputFile outputFile");
        }

    }

    /**
     * Listener Service, run in a separate thread
     */
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

        /**
         * run function, inherited from Thread Class
         */
        public void run() {
            while(true) {
                try {
                    Socket sock = listener.accept();
                    System.out.println("New slave connected");
                    SDSlave aSlave = new SDSlave(sock.getInetAddress(), sock.getPort());
                    aSlave.setReader(new BufferedReader(new InputStreamReader(sock.getInputStream())));
                    aSlave.setWriter(new PrintWriter(sock.getOutputStream()));
                    synchronized (slaveList){
                        slaveList.add(aSlave);
                    }
                }catch (IOException e){
                    System.err.println("fail to establish a socket with a slave node");
                    e.printStackTrace();
                }
            }
        }

    }

}
