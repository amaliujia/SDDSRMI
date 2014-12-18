import javax.sound.midi.SysexMessage;
import java.io.*;

public class SDMigratableProcess implements MigratableProcesses
{
    private TransactionalFileInputStream  inFile;
    private TransactionalFileOutputStream outFile;
    private String query;
    private boolean finished;

    private volatile boolean suspending;

    public SDMigratableProcess(String args[]) throws Exception
    {
      /*  if (args.length != 3) {
            System.out.println("usage: SDProcess <queryString> <inputFile> <outputFile>");
            throw new Exception("Invalid Arguments");
        }
*/
       // query = args[0];
        inFile = new TransactionalFileInputStream(args[0]);
        outFile = new TransactionalFileOutputStream(args[1], false);
        finished = false;
    }

    public void run()
    {
        InputStreamReader streamReader = new InputStreamReader(inFile);
        BufferedReader in = new BufferedReader(streamReader);
        DataOutputStream out = new DataOutputStream(((outFile)));

        try {
            while (!suspending) {
                String line = in.readLine();
               // if (line == null) continue;
               // if (line.contains(query)) {
                //System.out.println(line);
                out.writeBytes(line);
                // System.out.println(line);
               // }
                // Make grep take longer so that we don't require extremely large files for interesting results
                try {
                   Thread.sleep(800);
                } catch (InterruptedException e) {
                    // ignore it
                }
            }
        } catch (EOFException e) {
            //End of File
        } catch (IOException e) {
            System.out.println ("SDProcess: Error: " + e);
        }
        suspending = false;
        finished = true;

        try {
            out.flush();
            //out.close();
        }
        catch (IOException ex){
            System.out.println("writing failure...");
        }
        //outFile.closeFileStream();
        System.out.println("finish writing...");
    }

    public void suspend()
    {
        suspending = true;

        try {
            close();
        }catch (IOException e){
            e.printStackTrace();
        }


        while (suspending){
            //System.out.println("**");
        };
    }

    private void close() throws IOException {
        inFile.close();
        outFile.close();
        inFile.setMigaratable(true);
        outFile.setMigaratable(true);
    }

    public boolean finished() {
        return this.finished;
    }


}