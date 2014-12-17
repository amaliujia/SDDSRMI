/**
 * KANG.
 */

import sun.jvm.hotspot.debugger.ProcessInfo;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
public class SDSlaveNode {
    private OutputStreamWriter osw;
    private BufferedReader bis;
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
            bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            osw = new OutputStreamWriter(socket.getOutputStream());
        }
        catch(IOException ex) {
            System.out.println("MasterAddress or MasterPort is wrong");
        }
        System.out.print("Connection success");
    }

    public void disconnect(){
        try{
            bis.close();
            osw.close();
            socket.close();
        }
        catch (IOException ex)
        {
            System.out.println("disconnection error");
        }
        System.out.print("disConnection success");
    }


    public void slaveService()throws IOException{
        String[] args = null;
        String command;
        while((command = bis.readLine())!= null ){
            args = command.split(" ");
            if (args[0].equals("ps")){
                for (Integer processID : processTable.keySet()){
                    SDprocessInfo singleProcess = processTable.get(processID);
                    if (singleProcess.process)
                }
            }
            else if (args[0].equals("exit")){

            }
            else if (args[0].equals("")){

            }

        }



    }



}
