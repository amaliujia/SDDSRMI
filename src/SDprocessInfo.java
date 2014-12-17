/**
 * Created by kanghuang on 12/16/14.
 */

public class SDprocessInfo {

    public SDMigratableProcess process = null;
    public SDProcessStatus status = null;

    public SDprocessInfo(SDProcessStatus status, SDMigratableProcess process){
        this.status = status;
        this.process = process;
    }

}
