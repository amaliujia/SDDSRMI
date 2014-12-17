import java.io.Serializable;

/**
 * Created by amaliujia on 14-12-14.
 */

public interface MigratableProcesses extends Runnable, Serializable{

    // file operation
    public abstract void run();

    // suspending this function
    public abstract void suspend();

    // check if this process finished
    public boolean finished();

}
