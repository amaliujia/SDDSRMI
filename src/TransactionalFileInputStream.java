import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by amaliujia on 14-12-14.
 */
public class TransactionalFileInputStream extends InputStream implements Serializable {

    public TransactionalFileInputStream(String arg){

    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
