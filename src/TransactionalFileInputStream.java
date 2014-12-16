import java.io.*;

/**
 * Created by amaliujia on 14-12-14.
 */
public class TransactionalFileInputStream extends InputStream implements Serializable {

    private String fileName;
    private RandomAccessFile randomAccessFile;
    private long offset;
    private boolean migratable;

    public TransactionalFileInputStream(String arg){
        this.fileName = arg;

        try{
            this.randomAccessFile = new RandomAccessFile(this.fileName, "rws");
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file " + this.fileName + " in file system");
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.err.println("illegal arguments");
            e.printStackTrace();
        } catch (SecurityException e){
            System.err.println("Security problem");
            e.printStackTrace();
        }

        offset = 0L;
        migratable = false;
    }


    public int read() throws IOException {
        if(migratable){
            this.randomAccessFile = new RandomAccessFile(this.fileName, "rws");
            migratable = false;
        }

        int readBytes;
        this.randomAccessFile.seek(offset);
        readBytes = this.randomAccessFile.read();
        if(readBytes != -1){
           offset++;
        }

        return readBytes;
    }

    public void closeFileStream(){

        try {
            this.randomAccessFile.close();
        } catch (IOException e) {
             e.printStackTrace();
        }

    }

    public boolean isMigaratable(){
         if(migratable){
             return true;
         }
         return false;
    }

    public long getOffset(){
        return offset;
    }
}
