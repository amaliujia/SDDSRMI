/**
 * KANG.
 */

import sun.jvm.hotspot.debugger.ProcessInfo;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
public class SDSlaveNode {
    private PrintWriter pw;
    private BufferedReader bs;
    private String masterAddress;
    private int masterPort;
    private Socket socket;
    private TreeMap<Integer, SDprocessInfo> processTable = new TreeMap<Integer, SDprocessInfo>();
    private int processID;

    public SDSlaveNode(String masterAddress, int masterPort){
        this.masterAddress = masterAddress;
        this.masterPort = masterPort;
    }

    public void connect(){
        try{
            socket = new Socket(masterAddress, masterPort);
            bs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException ex) {
            System.out.println("MasterAddress or MasterPort is wrong");
        }
        System.out.print("Connection success");
    }

    public void disconnect(){
        try{
            bs.close();
            pw.close();
            socket.close();
        }
        catch (IOException ex)
        {
            System.out.println("disconnection error");
        }
        System.out.print("disConnection success");
    }


    public void slaveService()throws IOException, ClassNotFoundException{
        String[] args = null;
        String command;
        while((command = bs.readLine())!= null ){
            args = command.split(" ");
            if (args[0].equals("ps")){
                for (Integer processID : processTable.keySet()){
                    SDprocessInfo singleProcess = processTable.get(processID);
                    if (singleProcess.process.finished()){
                        pw.write("$ " + processID +  "   " + SDProcessStatus.TERMINATED + "\n");
                    }
                    else{
                        pw.write("$ " + processID +  "   " + singleProcess.status +  "\n");
                    }
                }
            }
            else if (args[0].equals("resume")){
                FileInputStream in = new FileInputStream(args[1] + args[2] + args[3] + ".obj");
                ObjectInputStream inObj = new ObjectInputStream(in);
                SDMigratableProcess mpIn = (SDMigratableProcess)inObj.readObject();
                in.close();
                inObj.close();
                Thread newProcess = new Thread(mpIn);
                SDprocessInfo processInfo = new SDprocessInfo(SDProcessStatus.RUNNING, mpIn);
                this.processTable.put(this.processID, processInfo);
                this.processID++;
                newProcess.start();
            }
            else if (args[0].equals("suspend")){ // suspend
                int migratableProcessID = -1;
                try{
                    migratableProcessID = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException ex){
                    System.err.println("process not found! Please check.");
                }
                SDprocessInfo processInfo = this.processTable.get(migratableProcessID);
                FileOutputStream out = new FileOutputStream(args[1] + ".obj");
                ObjectOutputStream outObj = new ObjectOutputStream(out);
                outObj.writeObject(processInfo.process);
                outObj.flush();
            }
            else if (args[0].equals("exit")){

            }
            else if (args[0].equals("")){

            }

        }



    }



}
