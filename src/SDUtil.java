import static java.lang.System.exit;

/**
 * Created by amaliujia on 14-12-13.
 */
public class SDUtil {

    static String inputFilePath = "input/WhatIf.txt";
    static String outputFilePath = "whatIf.txt";

    static void fatalError (String message) {
        System.err.println (message);
        exit(1);
    }
}
