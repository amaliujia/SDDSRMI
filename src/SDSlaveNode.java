/**
 * KANG.
 */

import sun.jvm.hotspot.debugger.ProcessInfo;

import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
public class SDSlaveNode {
    private PrintWriter pw;
    private BufferedReader bs;
    private String masterAddress;
    private int masterPort;
    private Socket socket;
    private TreeMap<Integer, SDProcessInfo> processTable = new TreeMap<Integer, SDProcessInfo>();
    private int processID;
    private String localHost;
    private int slavePort;
    public SDSlaveNode(String masterAddress, int masterPort){
        this.masterAddress = masterAddress;
        this.masterPort = masterPort;
    }

    public SDSlaveNode(String masterAddress, int masterPort, int slavePort){
        this.masterAddress = masterAddress;
        this.masterPort = masterPort;
        this.localHost = localHost;
        this.slavePort = slavePort;
    }

    public void connect(){
        try{
            socket = new Socket(masterAddress, masterPort);
            bs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            slavePort = socket.getLocalPort();
        }
        catch(IOException ex) {
            System.out.println("MasterAddress or MasterPort is wrong");
        }
        System.out.println("Connection success");
    }

    public void disconnect(){
        try{
            bs.close();
            pw.close();
            socket.close();
        }
        catch (IOException ex)
        {
            System.out.println("disConnection error\n");
        }
        System.out.println("disConnection success");
    }


    public void slaveService()throws IOException, ClassNotFoundException{
        String[] args = null;
        String command = null;
        System.out.println("Service start...");
       // int count = 0;
        while(true){
            try {
                System.out.println("Waiting...");
                command = bs.readLine();
                if (command == null) {
                    System.out.println("Interrupt!");
                    System.exit(0);
                }
          //      count++;
            }
            catch(IOException ex) {
                System.out.println("Exit!");
                    System.exit(0);
            }
            args = command.split(" ");
            if (args[0].equals("ps")){
                printState(args);
            }
            else if (args[0].equals("resume")){
                resumeProcess(args);
            }
            else if (args[0].equals("suspend")){
                suspendProcess(args);
            }
            else if (args[0].equals("start")){
                System.out.println("Successfully start a new process");
                startNewProcess(args);
            }
      //      System.out.println(count);
        }

    }

    public void printState(String[] args){
        for (Integer processID : processTable.keySet()){
            SDProcessInfo singleProcess = processTable.get(processID);
            if (singleProcess.process.finished()){
                pw.write("$ " + processID +  "   " + SDProcessStatus.TERMINATED + "\n");
            }
            else {
                pw.write("$ " + processID + "   " + singleProcess.status + "\n");
            }
        }
        //pw.write("ACK\n");
        if (processTable.isEmpty()){
            pw.write("$ no process" + "\n");
        }
        pw.flush();
    }

    public void resumeProcess(String[] args) throws IOException, ClassNotFoundException{
        FileInputStream in = new FileInputStream("/Users/hk/SD001/SDDSRMI/out/production/SDDSRMI/" + "0" + ".obj");
        ObjectInputStream inObj = new ObjectInputStream(in);
        MigratableProcesses mpIn = (MigratableProcesses)inObj.readObject();
        in.close();
        inObj.close();
        mpIn.resume();
        Thread newProcess = new Thread(mpIn);
       // mpIn.set_migrate();
        SDProcessInfo processInfo = new SDProcessInfo(SDProcessStatus.RUNNING, mpIn);
        this.processTable.put(this.processID, processInfo);
        this.processID++;
        newProcess.start();
        pw.write("ACK\n"); // ack signal
        pw.flush();
    }

    public void suspendProcess(String[] args) throws IOException{
        int migratableProcessID = -1;

        try{
            migratableProcessID = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException ex){
            System.err.println("processID error! Please check.");
        }

        SDProcessInfo processInfo = this.processTable.get(migratableProcessID);
        if (processInfo == null){
            System.err.print("process not found! Please check.");
        }
        processInfo.process.suspend();
       // System.out.print("s");
        processInfo.status = SDProcessStatus.SUSPENDING;

        FileOutputStream out = new FileOutputStream(args[1] + ".obj");
        ObjectOutputStream outObj = new ObjectOutputStream(out);
        outObj.writeObject(processInfo.process);
        outObj.flush();
        outObj.close();
        out.close();
        processInfo.process.finish();
        pw.write("ACK\n"); // ack signal
        pw.flush();

        this.processTable.remove(migratableProcessID);

    }

    public void startNewProcess(String[] args) throws ClassNotFoundException{
        MigratableProcesses newProcess = null;
        try {
            System.out.println(args[0] + " " + args[1] + " " + args[2]); // 1 inputFileName, 2 outputFileName
            Class<?> newProcessClass =  Class.forName(SDMigratableProcess.class.getName());
            Object[] processArgs = {Arrays.copyOfRange(args, 1, 3)}; // three parameter
            //System.out.println(processArgs[0] + " " + processArgs[1]);
            newProcess = (MigratableProcesses)newProcessClass.getConstructor(String[].class).newInstance(processArgs);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Could not find class " + args[2]);
            e.printStackTrace();
            return;
        } catch (SecurityException e) {
            System.out.println("Security Exception getting constructor for "
                    + args[2]);
            return;
        } catch (NoSuchMethodException e) {
            System.out.println("Could not find proper constructor for "
                    + args[2]);
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal arguments for " + args[2]);
            return;
        } catch (InstantiationException e) {
            System.out.println("Instantiation Exception for " + args[2]);
            return;
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access exception for " + args[2]);
            return;
        } catch (InvocationTargetException e) {
            System.out.println("Invocation target exception for " + args[2]);
            return;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        Thread newThread = new Thread(newProcess);
        SDProcessInfo processInfo = new SDProcessInfo(SDProcessStatus.RUNNING, newProcess);
        this.processTable.put(this.processID, processInfo);
        this.processID++;
        newThread.start();
        //pw.println("Start writing...");
        //pw.flush();
    }


}
