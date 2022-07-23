package gitlet;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author Guang Hou
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> {
                validateNumArgs("init", args, 1);
                Repository.init();
            }
            case "add" -> {
                Repository.checkInitialization();
                validateNumArgs("add", args, 2);
                String fileName = args[1];
                Repository.add(fileName);
            }
            case "commit" -> {
                Repository.checkInitialization();
                validateNumArgs("commit", args, 2);
                String message = args[1];
                Repository.makeCommit(message);
            }
            case "rm" -> {
                Repository.checkInitialization();
                validateNumArgs("add", args, 2);
                String fileName = args[1];
                Repository.rmFile(fileName);
            }
            case "log" -> {
                Repository.checkInitialization();
                validateNumArgs("log", args, 1);
                Repository.showLocalLog();
            }
            case "global-log" -> {
                Repository.checkInitialization();
                validateNumArgs("global-log", args, 1);
                Repository.showGlobalLog();
            }
            case "find" -> {
                Repository.checkInitialization();
                validateNumArgs("find", args, 2);
                String message = args[1];
                Repository.findCommitFromMessage(message);
            }
            case "status" -> {
                Repository.checkInitialization();
                validateNumArgs("status", args, 1);
                Repository.showStatus();
            }
            case "checkout" -> {
                Repository.checkInitialization();
                if (args.length == 2) {
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else if (args.length == 3) {
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4) {
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkoutCommitAndFile(commitID, fileName);
                } else {
                    throw new RuntimeException("Incorrect operands.");
                }
            }
            case "branch" -> {
                Repository.checkInitialization();
                validateNumArgs("branch", args, 2);
            }
            case "rm-branch" -> {
                Repository.checkInitialization();
                validateNumArgs("rm-branch", args, 2);
            }
            case "reset" -> {
                Repository.checkInitialization();
                validateNumArgs("reset", args, 1);
            }
            case "merge" -> {
                Repository.checkInitialization();
                validateNumArgs("merge", args, 2);
            }
            case "print" -> {
                Repository.checkInitialization();
                Repository.printVariables();
            }
            default -> exitWithError("No command with that name exists.");
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
