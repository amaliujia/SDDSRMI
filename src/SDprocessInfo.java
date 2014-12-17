/**
 * Created by kanghuang on 12/16/14.
 */

public class SDProcessInfo {

    public SDMigratableProcess process = null;
    public SDProcessStatus status = null;

    public SDProcessInfo(SDProcessStatus status, SDMigratableProcess process){
        this.status = status;
        this.process = process;
    }

}
