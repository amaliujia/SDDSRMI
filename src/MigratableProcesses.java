import java.io.Serializable;

/**
 * Created by amaliujia on 14-12-14.
 */

public interface MigratableProcesses extends Runnable, Serializable{

    public abstract void run();

    public abstract void suspend();

}
