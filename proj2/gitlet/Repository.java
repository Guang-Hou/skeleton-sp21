package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * This class handles all the input command from the user
 * by reading/writing from/to the correct files,
 * setting up persistence, and additional error checking.
 * All the file manipulation related work is done here.
 *
 * @author Guang Hou
 */
public class Repository {
    /* The current working directory.
     * It provides a way to access other files after adding the relevant relative path. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /* The hidden `.gitlet` directory. This is where all the persistence files will be stored. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");
    /* The .gitlet/commits directory to store serialized Commit objects. */
    private static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /* The .gitlet/blobs directory to store file blobs. */
    private static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /* The .gitlet/stagingArea directory. */
    private static final File STAGING_DIR = join(GITLET_DIR, "stagingArea");

    /* File storing the HashMap<String, String> of branchName : commitID. */
    private static File branchesFile = join(GITLET_DIR, "BRANCHES");
    /* File storing the string of the head Commit ID. */
    private static File headFile = join(GITLET_DIR, "HEAD");
    /* File storing the string of the active branch name. */
    private static File activeBranchFile = join(GITLET_DIR, "ACTIVE_BRANCH");
    /* File storing HashMap<String, String> of fileName : fileHash
     * for all files staged to add. */
    private static File addFile = join(STAGING_DIR, "ADD");
    /* File storing HashMap<String, String> of fileName : fileHash
     * for all files staged to remove. */
    private static File rmFile = join(STAGING_DIR, "RM");

    /* HashMap<String, String> for branchName : commitID. */
    private static HashMap<String, String> branchesMap = new HashMap<>();
    /* Head Commit hash ID. */
    private static String headID;
    /* Active branch name in String. */
    private static String activeBranchName;
    /* HashMap<String, String> of fileName : fileHash
     * for all files staged to add. */
    private static HashMap<String, String> addFileMap = new HashMap<>();
    /* HashMap<String, String> of fileName : fileHash
     * for all files staged to remove. */
    private static HashMap<String, String> rmFileMap = new HashMap<>();

    /* The head Commit object. This variable is not stored as file. */
    private static Commit headCommit;

    /**
     * Create the necessary directories and make the initial commit.
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println(
                    "A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        STAGING_DIR.mkdir();

        Commit initial = new Commit();
        String hash = saveCommitToSHA1Name(COMMITS_DIR, initial);

        branchesMap.put("master", hash);
        activeBranchName = "master";
        headID = hash;

        saveStaticVariableFiles();
    }

    /**
     * Check if .getlet directory is initialized.
     * It should be called before any gitlet operations except init.
     */
    public static void checkInitialization() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Save the Serializable object to the destination folder.
     * Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
     * Return the SHA1 hash.
     *
     * @param destFolder The destination folder.
     * @return The SHA1 hash of the serialized file.
     */
    public static String saveCommitToSHA1Name(File destFolder, Commit c) {
        File tempFile = join(destFolder, "temp");
        writeObject(tempFile, c);
        String fileContents = readContentsAsString(tempFile);
        String hash = sha1(fileContents);
        File fileNewName = join(destFolder, hash);
        // rename the file to its hash
        tempFile.renameTo(fileNewName);
        return hash;
    }

    /**
     * Copy the file to the destination folder.
     * Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
     * Return the SHA1 hash.
     *
     * @param destFolder The destination folder.
     * @return The file SHA1 hash.
     */
    public static String copyFileToSHA1Name(File destFolder, File f) {
        String fileContents = readContentsAsString(f);
        String hash = sha1(fileContents);
        File fileNewName = join(destFolder, hash);
        // if the fileBlob already exists, return its hash directly
        if (fileNewName.exists()) {
            return hash;
        }
        // copy the file to the destFolder and rename to it hash.
        // This will not replace the file if it exits.
        try {
            Files.copy(f.toPath(), fileNewName.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * Read the static variables from their corresponding files.
     * This is used before any function that requires these variables.
     */
    public static void readStaticVariables() {
        branchesMap = readObject(branchesFile, HashMap.class);
        activeBranchName = readContentsAsString(activeBranchFile);
        headID = readContentsAsString(headFile);
        headCommit = readCommitFromFile(headID);
        addFileMap = readObject(addFile, HashMap.class);
        rmFileMap = readObject(rmFile, HashMap.class);
    }

    /**
     * Write the static variable files contents to the corresponding files.
     */
    public static void saveStaticVariableFiles() {
        writeObject(branchesFile, branchesMap);
        writeContents(headFile, headID);
        writeContents(activeBranchFile, activeBranchName);
        writeObject(addFile, addFileMap);
        writeObject(rmFile, rmFileMap);
    }

    /**
     * Read from the file in COMMITS_DIR to a Commit object.
     * The file name is commitID in String.
     */
    public static Commit readCommitFromFile(String commitID) {
        File f = join(COMMITS_DIR, commitID);
        if (!f.exists()) {
            return null;
        }
        Commit c = readObject(f, Commit.class);
        return c;
    }

    /**
     * Helper function for debugging.
     * This prints the stabic variables contents.
     */
    public static void printVariables() {
        readStaticVariables();
        StringBuilder s = new StringBuilder();
        s.append("branchesMap: ").append(branchesMap + "\n").
                append("headID: ").append(headID + "\n").
                append("headCommit: \n").append(headCommit + "\n").
                append("addFilesMap: ").append(addFileMap + "\n").
                append("rmFilesMap: ").append(rmFileMap + "\n");
        System.out.println(s);
    }

    /**
     * Stage a file to be added in the repository.
     * Add the file to blobs folder and fileName:hash to the addFileMap.
     *
     * @param fileName The file to be staged for add.
     */
    public static void add(String fileName) {
        File f = join(CWD, fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // this will not copy the file if the fileBlob with the same hash name already exits.
        String hash = copyFileToSHA1Name(BLOBS_DIR, f);

        readStaticVariables();
        Map<String, String> headCommitBlobs = headCommit.getBlobs();

        // Update addFileMap
        // case 1: If the headCommit blobs is null or the blobs doesn't have this file hash,
        // add this fileName:hash entry to addFileMap.
        if (headCommitBlobs == null || !headCommitBlobs.containsValue(hash)) {
            addFileMap.put(fileName, hash);
        }
        // case 2: If headCommit blobs (fileName:hash) have this hash.
        else {
            // The existing fileBlob doesn't have this fileName
            if (!headCommitBlobs.containsKey(fileName)) {
                addFileMap.put(fileName, hash);
            }
            // The fileBlob already exists.
            else {
                // If the fileName:hash exists, no need to add.
                // If it is in the addFilesmap, remove it.
                if (headCommitBlobs.get(fileName).equals(hash)) {
                    if (addFileMap.containsKey(fileName)) {
                        addFileMap.remove(fileName);
                    }
                }
                // If hash doesn't match, we need to update.
                else {
                    addFileMap.put(fileName, hash);
                }
            }
        }

        // Update rmFilesMap
        if (rmFileMap.containsKey(fileName)) {
            rmFileMap.remove(fileName);
        }

        saveStaticVariableFiles();
    }

    /**
     * Stage a file to be removed from the repository.
     *
     * @param fileName
     */
    public static void rmFile(String fileName) {
        readStaticVariables();
        HashMap<String, String> headCommitBlobs = headCommit.getBlobs();

        if (addFileMap != null && addFileMap.containsKey(fileName)) {
            addFileMap.remove(fileName);
        } else if (headCommitBlobs != null && headCommitBlobs.containsKey(fileName)) {
            String hash = headCommit.getBlobs().get(fileName);
            rmFileMap.put(fileName, hash);
            File f = join(CWD, fileName);
            if (f.exists()) {
                restrictedDelete(f);
            }
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        saveStaticVariableFiles();
    }


    /**
     * Create a Commit object with the provided message.
     * Save the Commit object to commits folder.
     *
     * @param message The user input message for this commit.
     */
    public static void makeCommit(String message) {
        readStaticVariables();
        Commit newCommit = new Commit(headID, headCommit);

        if (addFileMap.isEmpty() && rmFileMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // Update message in the Commit object.
        newCommit.setMessage(message);

        // Update fileBlobs of the Commit instance variable.
        HashMap<String, String> oldFileBlobs = headCommit.getBlobs();
        HashMap<String, String> newFileBlobs = new HashMap<>();
        if (oldFileBlobs != null) {
            newFileBlobs = new HashMap<>(oldFileBlobs);
        }

        for (Map.Entry<String, String> entry : addFileMap.entrySet()) {
            String fileName = entry.getKey();
            String fileHash = entry.getValue();
            newFileBlobs.put(fileName, fileHash);
        }

        for (Map.Entry<String, String> entry : rmFileMap.entrySet()) {
            String fileName = entry.getKey();
            String fileHash = entry.getValue();
            if (newFileBlobs.containsKey(fileName)) {
                newFileBlobs.remove(fileName);
            }
        }
        newCommit.setBlobs(newFileBlobs);

        // Save commit object to the folder.
        String newCommitID = saveCommitToSHA1Name(COMMITS_DIR, newCommit);

        // Update headID and branch pointer to the new commitID.
        headID = newCommitID;
        branchesMap.put(activeBranchName, headID);

        // Clear stagingArea.
        addFileMap.clear();
        rmFileMap.clear();

        saveStaticVariableFiles();
    }


    /**
     * Print the log history of the headCommit.
     */
    public static void showLocalLog() {
        readStaticVariables();
        StringBuilder s = new StringBuilder();

        Commit cur = headCommit;
        String curID = headID;
        while (cur != null) {
            s.append(cur.toString(curID));
            ArrayList<String> parentCommitIDs = cur.getParentCommits();
            if (parentCommitIDs == null) {
                break;
            }
            String firstParentID = parentCommitIDs.get(0);
            curID = firstParentID;
            cur = readCommitFromFile(firstParentID);
        }

        saveStaticVariableFiles();
        System.out.println(s);
    }

    /**
     * Show all commits in no particular order.
     */
    public static void showGlobalLog() {
        StringBuilder log = new StringBuilder();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = readCommitFromFile(commitID);
            log.append(c.toString(commitID));
        }
        System.out.println(log);
    }

    /**
     * Show all commits in the order of timestamp.
     */
    public static void showGlobalLogInOrder() {
        Map<Commit, String> commitMap = new TreeMap<>();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = readCommitFromFile(commitID);
            commitMap.put(c, commitID);
        }

        StringBuilder s = new StringBuilder();
        for (Map.Entry<Commit, String> entry : commitMap.entrySet()) {
            Commit c = entry.getKey();
            String id = entry.getValue();
            s.append(c.toString(id));
        }

        System.out.println(s);
    }


    /**
     * Find all commit IDs having the provided message.
     *
     * @param message
     */
    public static void findCommitFromMessage(String message) {
        StringBuilder relatedCommits = new StringBuilder();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = readCommitFromFile(commitID);
            if (c.getMessage().equals(message)) {
                relatedCommits.append(commitID).append("\n");
            }
        }
        if (relatedCommits == null || relatedCommits.length() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(relatedCommits);
    }

    /**
     * Print the repository information such as branches, staged files etc.
     */
    public static void showStatus() {
        readStaticVariables();
        StringBuilder output = new StringBuilder();

        // Add all branch names.
        Set<String> allBranches = branchesMap.keySet();
        output.append("=== Branches ===\n").append("*" + activeBranchName + "\n");
        for (String b : allBranches) {
            if (!b.equals(activeBranchName)) {
                output.append(b + "\n");
            }
        }
        output.append("\n");

        // Add file names from addFileMap.
        output.append("=== Staged Files ===\n");
        Set<String> allAddFiles = addFileMap.keySet();
        for (String f : allAddFiles) {
            output.append(f + "\n");
        }
        output.append("\n");

        // Add file names from rmFileMap.
        output.append("=== Removed Files ===\n");
        Set<String> allRmFiles = rmFileMap.keySet();
        for (String f : allRmFiles) {
            output.append(f + "\n");
        }
        output.append("\n");

        // Add modified files but not staged for commit.
        output.append("=== Modifications Not Staged For Commit ===\n");
        output.append("\n");

        // Add untracked file names.
        output.append("=== Untracked Files ===\n");
        //output.append("\n");

        System.out.println(output);
    }

    /**
     * Change the file's contens according to its snapshot in the provided commitID.
     *
     * @param commitID The specific Commit's hash id.
     * @param fileName The file name in CWD.
     */
    public static void checkoutCommitAndFile(String commitID, String fileName) {
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        if (!commitIDs.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Commit c = readCommitFromFile(commitID);
        HashMap<String, String> fileBlobs = c.getBlobs();
        if (!fileBlobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String fileHash = fileBlobs.get(fileName);
        File f = join(BLOBS_DIR, fileHash);

        // copy the file to the CWD, and replace the existing target file
        File target = join(CWD, fileName);
        try {
            Files.copy(f.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change a file's content according to the headCommit.
     *
     * @param fileName
     */
    public static void checkoutFile(String fileName) {
        readStaticVariables();
        checkoutCommitAndFile(headID, fileName);
    }

    /**
     * Change the CWD contents according to the branch's latest commit.
     *
     * @param branchName
     */
    public static void checkoutBranch(String branchName) {
        readStaticVariables();

        if (!branchesMap.keySet().contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        if (branchName.equals(activeBranchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        // Copy files from the branch head Commit to CWD
        String branchHeadID = branchesMap.get(branchName);
        resetCommitFiles(branchHeadID);

        // Update activeBranch
        headID = branchHeadID;
        activeBranchName = branchName;
        addFileMap.clear();
        rmFileMap.clear();

        saveStaticVariableFiles();
    }

    /**
     * Create a new branch and point it to the headCommit.
     *
     * @param branchName
     */
    public static void createBranch(String branchName) {
        readStaticVariables();
        if (branchesMap.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchesMap.put(branchName, headID);
        saveStaticVariableFiles();
    }

    /**
     * Remove the provided branch from the repository.
     *
     * @param branchName
     */
    public static void rmBranch(String branchName) {
        readStaticVariables();
        branchesMap.remove(branchName);
        saveStaticVariableFiles();
    }

    /**
     * Change CWD contens according to the provided commit id.
     * Update head pointer and branch pointer.
     *
     * @param preCommitID
     */
    public static void resetCommit(String preCommitID) {
        readStaticVariables();
        resetCommitFiles(preCommitID);

        // Update head pointer
        headID = preCommitID;
        branchesMap.put(activeBranchName, preCommitID);
        addFileMap.clear();
        rmFileMap.clear();

        saveStaticVariableFiles();
    }

    /**
     * Helper function to change CWD contens based on the provided commit id.
     *
     * @param preCommitID The target commit object hash id.
     */
    public static void resetCommitFiles(String preCommitID) {
        readStaticVariables();

        Commit preCommit = readCommitFromFile(preCommitID);
        if (preCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        // Check if there is any untracked files in CWD
        List<String> filesInCWD = plainFilenamesIn(CWD);
        for (String file : filesInCWD) {
            if (!headCommit.getBlobs().containsKey(file)) {
                System.out.println(
                        "There is an untracked file in the way;"
                                + " delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // Deletes all files in CWD
        for (String file : filesInCWD) {
            restrictedDelete(join(CWD, file));
        }

        // Copy files from the preCommit blobs to CWD
        HashMap<String, String> fileBlobs = preCommit.getBlobs();

        if (fileBlobs != null) {
            for (Map.Entry<String, String> entry : fileBlobs.entrySet()) {
                String fileName = entry.getKey();
                String fileHash = entry.getValue();
                File f = join(BLOBS_DIR, fileHash);
                File newFile = join(CWD, fileName);
                // copy the file to the CWD and rename to its name
                try {
                    Files.copy(f.toPath(), newFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param branch
     */
    public static void merge(String branch) {

    }

    /**
     * @param branch1
     * @param branch2
     * @return
     */
    public static Commit findLatestCommonAncestor(String branch1, String branch2) {
        return null;
    }

    /**
     * @param file1
     * @param file2
     */
    public static void handleConflict(String file1, String file2) {

    }

    /**
     * @param c
     * @return
     */
    public static HashSet<String> toSet(Commit c) {
        return null;
    }
}
