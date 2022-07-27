package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * @author Guang Hou
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains the input command
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> {
                validateNumArgs(args, 1);
                Repository.init();
            }
            case "add" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String fileName = args[1];
                Repository.add(fileName);
            }
            case "commit" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String message = args[1];
                Repository.makeCommit(message);
            }
            case "rm" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String fileName = args[1];
                Repository.rmFile(fileName);
            }
            case "log" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 1);
                Repository.showLocalLog();
            }
            case "global-log" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 1);
                Repository.showGlobalLog();
            }
            case "find" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String message = args[1];
                Repository.findCommitFromMessage(message);
            }
            case "status" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 1);
                Repository.showStatus();
            }
            case "checkout" -> {
                Repository.checkInitialization();
                if (args.length == 2) {
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else if (args.length == 3 && args[1].equals("--")) {
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                } else if (args.length == 4 && args[2].equals("--")) {
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkoutCommitAndFile(commitID, fileName);
                } else {
                    System.out.println("Incorrect operands.");
                }
            }
            case "branch" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String branchName = args[1];
                Repository.createBranch(branchName);
            }
            case "rm-branch" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String branchName = args[1];
                Repository.rmBranch(branchName);
            }
            case "reset" -> {
                Repository.checkInitialization();
                validateNumArgs(args, 2);
                String commitID = args[1];
                Repository.resetCommit(commitID);
            }
            case "merge" -> {
                Repository.checkInitialization();
                validateNumArgs( args, 2);
                String branchName = args[1];
                Repository.merge(branchName);
            }
            case "print" -> {
                Repository.checkInitialization();
                Repository.printVariables();
            }
            default -> System.out.println("No command with that name exists.");
        }
    }


    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
        }
    }

    public static void exitWithError(String message) {
        if (message != null && !message.isEmpty()) {
            System.out.println(message);
        }
        System.exit(0);
    }
}
