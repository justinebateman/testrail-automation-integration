package justinebateman.github.io.testrailintegration.util;

import org.testng.Reporter;

public class Log
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[92m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // log to TestNG and don't print to console
    public static void log(String message)
    {
        logToTestNG(message);
    }

    // print to console and don't log to TestNG
    public static void print(String message)
    {
        printColoredMessage(ANSI_PURPLE, message);
    }

    // log to TestNG and print coloured message to console
    public static void logAndPrint(String color, String message)
    {
        printColoredMessage(color, message);
        logToTestNG(message);
    }

    // log to TestNG and print coloured message to console with default color
    public static void logAndPrint(String message)
    {
        logAndPrint(ANSI_PURPLE, message);
    }

    //success
    public static void success(String message)
    {
        logAndPrint(ANSI_GREEN, message);
    }

    //assertion
    public static void assertion(String message)
    {
        success("Assertion passed: " + message);
    }

    //info
    public static void info(String message)
    {
        logAndPrint(ANSI_PURPLE, message);
    }

    //error
    public static void error(String message)
    {
        logAndPrint(ANSI_RED, message);
    }

    //debug
    public static void debug(String message)
    {
        logAndPrint(ANSI_CYAN, message);
    }

    //warning
    public static void warning(String message)
    {
        logAndPrint(ANSI_YELLOW, message);
    }

    public static void printColoredMessage(String color, String message)
    {
        System.out.println(color + message + ANSI_RESET);
    }

    private static void logToTestNG(String message)
    {
        Reporter.log(message);
    }

}
