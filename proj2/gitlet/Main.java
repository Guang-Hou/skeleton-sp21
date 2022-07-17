package gitlet;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args == null) {
            exitWithError("Please enter a command.");
        }

        Repository.setupPersistence();

        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs("init", args, 0);
                break;
            case "add":
                Repository.checkInitialization();
                validateNumArgs("add", args, 1);
                break;
            case "commit":
                Repository.checkInitialization();
                validateNumArgs("commit", args, 1);
                break;
            case "rm":
                Repository.checkInitialization();
                validateNumArgs("rm", args, 1);
                break;
            case "log":
                Repository.checkInitialization();
                validateNumArgs("log", args, 0);
                break;
            case "global-log":

            case "find":

            case "status":

            case "checkout":

            case "branch":

            case "rm-branch":

            case "reset":


            case "merge":



            default:
                exitWithError("No command with that name exists.");
        }
    }


    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException("Incorrect operands.");
        }
    }

    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(-1);
    }
}
