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
            socket = new Socket(masterAddress, masterPort, null, slavePort);
            bs = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

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
        while(true){
            try {
                System.out.println("Waiting...");
                command = bs.readLine();
                if (command == null){
                    System.out.println("Interrupt!");
                    System.exit(0);
                }
             //   System.out.println("Waiting...");
                System.out.println(command);
            }
            catch(IOException ex) {
                System.out.println("Exit!");
                    System.exit(0);
            }
            args = command.split(" ");
            System.out.println(args[0]);
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

        }
    }

    public void printState(String[] args){
        for (Integer processID : processTable.keySet()){
            SDProcessInfo singleProcess = processTable.get(processID);
            if (singleProcess.process.finished()){
                pw.write("$ " + processID +  "   " + SDProcessStatus.TERMINATED + "\n");
            }
            else{
                pw.write("$ " + processID +  "   " + singleProcess.status +  "\n");
            }
            pw.flush();
        }
    }

    public void resumeProcess(String[] args) throws IOException, ClassNotFoundException{
        FileInputStream in = new FileInputStream(args[1] + args[2] + args[3] + ".obj");
        ObjectInputStream inObj = new ObjectInputStream(in);
        SDMigratableProcess mpIn = (SDMigratableProcess)inObj.readObject();
        in.close();
        inObj.close();
        Thread newProcess = new Thread(mpIn);
        SDProcessInfo processInfo = new SDProcessInfo(SDProcessStatus.RUNNING, mpIn);
        this.processTable.put(this.processID, processInfo);
        this.processID++;
        newProcess.start();
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
        processInfo.status = SDProcessStatus.SUSPENDING;
        FileOutputStream out = new FileOutputStream(args[1] + ".obj");
        ObjectOutputStream outObj = new ObjectOutputStream(out);
        outObj.writeObject(processInfo.process);
        outObj.flush();
        outObj.close();
        out.close();
        pw.print("suspending finished\n"); // ack signal
        pw.flush();
        this.processTable.remove(processID);
    }

    public void startNewProcess(String[] args) throws ClassNotFoundException{
        SDMigratableProcess newProcess = null;
        try {
            System.out.println(args[0] + " " + args[1] + " " + args[2]);
            Class<SDMigratableProcess> newProcessClass = (Class<SDMigratableProcess>) Class.forName(SDMigratableProcess.class.getName());
            Object[] processArgs = {Arrays.copyOfRange(args, 1, 3)}; // three parameter
            //System.out.println(processArgs[0] + " " + processArgs[1]);
            newProcess = newProcessClass.getConstructor(String[].class).newInstance(processArgs);
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
            System.out.println("IIlegal access exception for " + args[2]);
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
