package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.StandardCopyOption.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * This class handles all the input command from user.
 * All the file manipulation related work is done here.
 *
 * @author Guang Hou
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The .gitlet/commits directory.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /**
     * The .gitlet/blobs directory.
     */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /**
     * The .gitlet/stagingArea directory.
     */
    public static final File STAGING_DIR = join(GITLET_DIR, "stagingArea");

    /* A file storing the branches information, Map<String, String> for branchName : commitID */
    public static File BRANCHES = join(GITLET_DIR, "branches");
    /* A file storing the string of the head Commit ID */
    public static File HEAD = join(GITLET_DIR, "HEAD");
    /* A file storing the string of the active branch name */
    public static File ACTIVE_BRANCH = join(GITLET_DIR, "activeBranch");
    /* A serialized file storing hashmap<String, String> stores fileName : hash for files staged to add */
    public static File addFiles = join(STAGING_DIR, "addFiles");
    /* A serialized file storing hashmap<String, String> stores fileName : hash for files staged to remove */
    public static File rmFiles = join(STAGING_DIR, "rmFiles");

    /* These four variables are stored as files. */
    public static HashMap<String, String> branchesMap = new HashMap<>();
    /* Head Commit hash ID */
    public static String headID;
    /* Active branch name */
    public static String activeBranch;
    public static HashMap<String, String> addFilesMap = new HashMap<>();
    public static HashMap<String, String> rmFilesMap = new HashMap<>();

    /* This variable is not stored as file. */
    public static Commit headCommit;

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        STAGING_DIR.mkdir();

        Commit initial = new Commit();
        String hash = saveObjectToSHA1Name(COMMITS_DIR, initial);

        branchesMap.put("master", hash);
        activeBranch = "master";
        headID = hash;

        saveStaticVariableFiles();
    }

    public static void checkInitialization() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Save the Serializable object to the destination folder,
     * Get the SHA1 hash of the file, and change the file name to the SHA1 hash
     * return the SHA1 hash
     *
     * @param DestinationFolder
     * @return
     */
    public static String saveObjectToSHA1Name(File DestinationFolder, Object ob) {
        File tempFile = join(DestinationFolder, "temp");
        writeObject(tempFile, (Serializable) ob);
        String fileContents = readContentsAsString(tempFile);
        String hash = sha1(fileContents);
        File fileNewName = join(DestinationFolder, hash);
        // rename the file to its hash
        tempFile.renameTo(fileNewName);
        return hash;
    }


    /**
     * Save the file to the destination folder as String,
     * Get the SHA1 hash of the file, and change the file name to the SHA1 hash
     * return the SHA1 hash
     *
     * @param DestinationFolder
     * @return
     */
    public static String copyFileToSHA1Name(File DestinationFolder, File f) {
        String fileContents = readContentsAsString(f);
        String hash = sha1(fileContents);
        File fileNewName = join(DestinationFolder, hash);
        // copy the file to the DestinationFolder and rename to it hash
        try {
            Files.copy(f.toPath(), fileNewName.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    /**
     * Read the the static variables from files
     * Can be called before any function that requires these variables
     */
    public static void readStaticVariables() {
        branchesMap = readObject(BRANCHES, HashMap.class);
        headID = readContentsAsString(HEAD);
        activeBranch = readContentsAsString(ACTIVE_BRANCH);
        headCommit = Commit.fromFile(headID);
        addFilesMap = readObject(addFiles, HashMap.class);
        rmFilesMap = readObject(rmFiles, HashMap.class);
    }

    // Helper function for debugging
    public static void printVariables() {
        readStaticVariables();
        StringBuilder s = new StringBuilder();
        s.append("branchesMap: ").append(branchesMap + "\n").
                append("headID: ").append(headID + "\n").
                append("headCommit: \n").append(headCommit + "\n").
                append("addFilesMap: ").append(addFilesMap + "\n").
                append("rmFilesMap: ").append(rmFilesMap + "\n");
        System.out.println(s);
    }

    /**
     * Update the static variable files based on current static variable contents
     */
    public static void saveStaticVariableFiles() {
        writeObject(BRANCHES, branchesMap);
        writeContents(HEAD, headID);
        writeContents(ACTIVE_BRANCH, activeBranch);
        writeObject(addFiles, addFilesMap);
        writeObject(rmFiles, rmFilesMap);
    }

    public static void add(String fileName) {
        File f = join(CWD, fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // this will not copy the file if the fileBlob with the same hash already exits
        String hash = copyFileToSHA1Name(BLOBS_DIR, f);

        readStaticVariables();
        Map<String, String> headCommitBlobs = headCommit.getBlobs();

        /* Update addFilesMap */
        // case 1: If the headCommit blobs is null or the blobs doesn't have this file hash, add this fileName:hash entry
        // case 2: If headCommit blobs (fileName:hash) have this hash. Then no need to add this fileBlob.
        //         If the fileName doesn't exist in the headCommit fileBlobs, then put the newFileName:hash to teh addFilesMap.
        //         If the fileName exists in the headCommit fileBlobs:
        //             If fileName and hash both match, and it exists is in addFilesMap, remove it.
        //             else we need to update the hash
        if (headCommitBlobs == null || !headCommitBlobs.containsValue(hash)) {
            addFilesMap.put(fileName, hash);
        } else {
            // The fileBlob doesn't have this fileName
            if (!headCommitBlobs.containsKey(fileName)) {
                addFilesMap.put(fileName, hash);
            } else {
                // if the fileName:hash exists, no need to add. If it is in the addFilesmap, remove it
                if (headCommitBlobs.get(fileName).equals(hash)) {
                    if (addFilesMap.containsKey(fileName)) {
                        addFilesMap.remove(fileName);
                    }
                } else { // hash doesn't match, we need to update
                    addFilesMap.put(fileName, hash);
                }
            }
        }

        /* Update rmFilesMap */
        if (rmFilesMap.containsKey(fileName)) {
            rmFilesMap.remove(fileName);
        }

        saveStaticVariableFiles();
    }

    public static void makeCommit(String message) {
        readStaticVariables();
        Commit newCommit = new Commit(headID, headCommit);

        if (addFilesMap.isEmpty() && rmFilesMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        if (message == null) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        /* update message */
        newCommit.setMessage(message);

        /* update file blobs */
        HashMap<String, String> oldFileBlobs = headCommit.getBlobs();
        HashMap<String, String> newFileBlobs = new HashMap<>();
        if (oldFileBlobs != null) {
            newFileBlobs = new HashMap<>(oldFileBlobs);
        }

        for (Map.Entry<String, String> entry : addFilesMap.entrySet()) {
            String fileName = entry.getKey();
            String fileHash = entry.getValue();
            newFileBlobs.put(fileName, fileHash);
        }

        for (Map.Entry<String, String> entry : rmFilesMap.entrySet()) {
            String fileName = entry.getKey();
            String fileHash = entry.getValue();
            if (newFileBlobs.containsKey(fileName)) {
                newFileBlobs.remove(fileName);
            }
        }
        newCommit.setBlobs(newFileBlobs);

        /* save commit object to the folder */
        String newCommitID = saveObjectToSHA1Name(COMMITS_DIR, newCommit);

        /* update head and branch information */
        headID = newCommitID;
        branchesMap.put(activeBranch, headID);

        /* Clear stagingArea */
        addFilesMap.clear();
        rmFilesMap.clear();

        saveStaticVariableFiles();
    }

    public static void rmFile(String fileName) {
        readStaticVariables();
        if (addFilesMap.containsKey(fileName)) {
            addFilesMap.remove(fileName);
        } else if (headCommit.getBlobs().containsKey(fileName)) {
            String hash = headCommit.getBlobs().get(fileName);
            rmFilesMap.put(fileName, hash);
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

    public static void showLocalLog() {
        readStaticVariables();
        StringBuilder s = new StringBuilder();

        Commit cur = headCommit;
        String curID = headID;
        while (cur != null) {
            s.append(cur.toString(curID)).append("\n");
            ArrayList<String> parentCommitIDs = cur.getParentCommits();
            if (parentCommitIDs == null) {
                break;
            }
            String firstParentID = parentCommitIDs.get(0);
            curID = firstParentID;
            cur = Commit.fromFile(firstParentID);
        }

        saveStaticVariableFiles();
        System.out.println(s);
        ;
    }

//    /* Helper function to put commits in an ordered tree */
//    public static TreeMap<String, String> createCommitBST() {
//        return null;
//    }

    /**
     * Show all commits in the order of timestamp.
     */
    public static void showGlobalLogInOrder() {
        Map<Commit, String> commitMap = new TreeMap<>();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = Commit.fromFile(commitID);
            commitMap.put(c, commitID);
        }

        StringBuilder s = new StringBuilder();
        for (Map.Entry<Commit, String> entry : commitMap.entrySet()) {
            Commit c = entry.getKey();
            String id = entry.getValue();
            s.append(c.toString(id));
        }

        System.out.println(s);
        ;
    }

    /**
     * Show all commits in no order.
     */
    public static void showGlobalLog() {
        StringBuilder log = new StringBuilder();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = Commit.fromFile(commitID);
            log.append(c.toString(commitID));
        }
        System.out.println(log);
        ;
    }

    public static void findCommitFromMessage(String message) {
        StringBuilder relatedCommits = new StringBuilder();
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            Commit c = Commit.fromFile(commitID);
            if (c.getMessage().equals(message)) {
                relatedCommits.append(commitID);
            }
        }
        if (relatedCommits == null) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(relatedCommits);
    }

    public static void showStatus() {
        readStaticVariables();
        StringBuilder output = new StringBuilder();

        Set<String> allBranches = branchesMap.keySet();
        StringBuilder otherBranches = new StringBuilder();

        output.append("=== Branches ===\n").append("*" + activeBranch + "\n");
        for (String b : allBranches) {
            if (!b.equals(activeBranch)) {
                output.append(b + "\n");
            }
        }

        output.append("=== Staged Files ===\n");
        Set<String> allAddFiles = addFilesMap.keySet();
        for (String f : allAddFiles) {
            output.append(f + "\n");
        }

        output.append("=== Removed Files ===\n");
        Set<String> allRmFiles = rmFilesMap.keySet();
        for (String f : allRmFiles) {
            output.append(f + "\n");
        }

        System.out.println(output);
    }

    public static void checkoutFile(String fileName) {
        readStaticVariables();
        HashMap<String, String> headCommitBlobs = headCommit.getBlobs();
        if (!headCommitBlobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileHash = headCommitBlobs.get(fileName);
        restrictedDelete(join(CWD, fileName));
        File f = join(BLOBS_DIR, fileHash);
        if (!f.renameTo(join(CWD, fileName))) {
            System.out.println("Could not copy the file to CWD.");
        }
    }

    public static void checkoutCommitAndFile(String commitID, String fileName) {
        List<String> commitIDs = plainFilenamesIn(COMMITS_DIR);
        if (!commitIDs.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Commit c = Commit.fromFile(commitID);
        HashMap<String, String> fileBlobs = c.getBlobs();
        if (!fileBlobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileHash = fileBlobs.get(fileName);
        restrictedDelete(join(CWD, fileName));
        File f = join(BLOBS_DIR, fileHash);
        if (!f.renameTo(join(CWD, fileName))) {
            System.out.println("Could not copy the file to CWD.");
        }
    }

    public static void checkoutBranch(String branch) {
        readStaticVariables();

        if (!branchesMap.keySet().contains(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        if (branch.equals(activeBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        // Check if there is any untracked files in CWD
        List<String> filesInCWD = plainFilenamesIn(CWD);
        for (String file : filesInCWD) {
            if (!headCommit.getBlobs().containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // Deletes all files in CWD
        for (String file : filesInCWD) {
            restrictedDelete(join(CWD, file));
        }

        // Copy files from the branch head Commit to CWD
        String branchHeadID = branchesMap.get(branch);
        Commit branchHead = Commit.fromFile(branchHeadID);
        HashMap<String, String> fileBlobs = branchHead.getBlobs();

        for (Map.Entry<String, String> entry : fileBlobs.entrySet()) {
            String fileName = entry.getKey();
            String fileHash = entry.getValue();
            File f = join(BLOBS_DIR, fileHash);
            if (!f.renameTo(join(CWD, fileName))) {
                System.out.println("Could not copy the file to CWD.");
            }
        }

        // Update activeBranch
        activeBranch = branch;
        addFilesMap.clear();
        rmFilesMap.clear();

        saveStaticVariableFiles();
    }

    public static void createBranch(String branch) {

    }

    public static void rmBranch(String branch) {

    }

    public static void resetCommit(String commitID) {

    }

    public static void merge(String branch) {

    }

    public static Commit findLatestCommonAncestor(String branch1, String branch2) {
        return null;
    }

    public static void handleConflict(String file1, String file2) {

    }

    public static HashSet<String> toSet(Commit c) {
        return null;
    }
}
