import java.io.*;

public class SDMigratableProcess implements MigratableProcesses
{
    private TransactionalFileInputStream  inFile;
    private TransactionalFileOutputStream outFile;
    private String query;

    private volatile boolean suspending;

    public SDMigratableProcess(String args[]) throws Exception
    {
        if (args.length != 3) {
            System.out.println("usage: SDProcess <queryString> <inputFile> <outputFile>");
            throw new Exception("Invalid Arguments");
        }

        query = args[0];
        inFile = new TransactionalFileInputStream(args[1]);
        outFile = new TransactionalFileOutputStream(args[2], false);
    }

    public void run()
    {
        InputStreamReader streamReader = new InputStreamReader(inFile);
        BufferedReader in = new BufferedReader(streamReader);
        PrintWriter out = new PrintWriter(outFile);

        try {
            while (!suspending) {
                String line = in.readLine();

                if (line == null) break;

                if (line.contains(query)) {
                    out.println(line);
                }
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
    }

    public void suspend()
    {
        suspending = true;
        while (suspending);
    }

}