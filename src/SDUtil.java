import static java.lang.System.exit;

/**
 * Created by amaliujia on 14-12-13.
 */
public class SDUtil {

    static String[] inputFilePath = {"/Users/hk/SD001/SDDSRMI/input/WhatIf.txt", "/Users/hk/SD001/SDDSRMI//input/Code.txt"};
    static String[] outputFilePath = {"/Users/hk/SD001/SDDSRMI/whatIf.txt", "/Users/hk/SD001/SDDSRMI/code.txt"};



    static void fatalError (String message) {
        System.err.println (message);
        exit(1);
    }
}
