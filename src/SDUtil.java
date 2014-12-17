import static java.lang.System.exit;

/**
 * Created by amaliujia on 14-12-13.
 */
public class SDUtil {

    static String inputFilePath = "/Users/hk/SD001/SDDSRMI/input/WhatIf.txt";
    static String outputFilePath = "/Users/hk/SD001/SDDSRMI/whatIf.txt";

    static void fatalError (String message) {
        System.err.println (message);
        exit(1);
    }
}
