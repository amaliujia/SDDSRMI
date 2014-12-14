import static java.lang.System.exit;

/**
 * Created by amaliujia on 14-12-13.
 */
public class SDUtil {
    static void fatalError (String message) {
        System.err.println (message);
        exit(1);
    }
}
