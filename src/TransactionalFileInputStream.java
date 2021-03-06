import java.io.*;

/**
 * Created by amaliujia on 14-12-14.
 * CND OFFSET 不减1000会莫名其妙的停止增长
 *
 */
public class TransactionalFileInputStream extends InputStream implements Serializable {
    private static final long serialVersionUID = 568680122;
    private String fileName;
    private transient RandomAccessFile randomAccessFile;
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
            System.err.println("Illegal arguments");
            e.printStackTrace();
        } catch (SecurityException e){
            System.err.println("Security problem");
            e.printStackTrace();
        }

        offset = 0L;
        migratable = false;
    }


    public int read() throws IOException {
        if(migratable || this.randomAccessFile == null ){
            System.out.println("Read" + this.fileName);
            this.randomAccessFile = new RandomAccessFile(this.fileName, "r");
            migratable = false;
            /***
             * to do list OFFSET 不减1000会莫名其妙的停止增长
             */
            if (offset > 1000)
              offset -= 1000;

            System.out.println(offset);
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


    public void setMigaratable(boolean flag){
        this.migratable = flag;
    }

    public long getOffset(){
        return offset;
    }
}
