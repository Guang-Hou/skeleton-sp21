package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * This class provides the way to represent Commit information.
 * It doesn't handle Commit instance file manipulation directly.
 *
 * @author Guang Hou
 */
public class Commit implements Serializable, Comparable<Commit> {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The CWD/.gitlet/commits directory.
     */
    public static final File COMMIT_DIR = join(CWD, ".gitlet", "commits");

    /* The message of this Commit. */
    private String message;
    private Date timestamp;
    /* The parent commits of current commit, stored as String in an Array */
    private ArrayList<String> parents = new ArrayList<>();
    /* The HashMap storing all the files in the format of fileName: hash */
    private HashMap<String, String> blobs = new HashMap<>();

    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parents = null;
        blobs = null;
    }

    public Commit(String parentCommitID, Commit parentCommit) {
        timestamp = new Date();
        parents.clear();
        parents.add(parentCommitID);
        Map<String, String> parentBlobs = parentCommit.getBlobs();
        if (parentBlobs != null) {
            blobs = new HashMap<String, String>(parentBlobs);
        }
    }

//    public Commit(String parentCommitID) {
//        Commit parent = fromFile(parentCommitID);
//        timestamp = new Date();
//        parentCommits.add(parentCommitID);
//        Map<String, String> parentBlobs = parent.getBlobs();
//        blobs = new HashMap<String, String>(parentBlobs);
//    }

    /**
     * Read from the file in COMMITS_DIR to a Commit object
     * The file name is commitID in String
     */
    public static Commit fromFile(String commitID) {
        File f = join(COMMIT_DIR, commitID);
        Commit c = readObject(f, Commit.class);
        return c;
    }

    /**
     * This is used for debugging purpose.
     */
    @Override
    public String toString() {
        return "Date - " + timestamp + "\n" +
                "message - " + message + "\n" +
                "parents - " + parents + "\n" +
                "blobs - " + blobs;
    }

    // This is used for log
    public String toString(String id) {
        String merge = "";

        if (parents != null && parents.size() > 1) {
            merge += "Merge: ";
            for (String parent : parents) {
                merge += parent.substring(0, 7) + " ";
            }
            merge += "\n";
        }

        SimpleDateFormat DateFor = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        String stringDate = DateFor.format(timestamp);

        return "===" + "\n" +
                "commit " + id + "\n" +
                merge +
                "Date: " + stringDate + "\n" +
                this.message + "\n";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getParentCommits() {
        return parents;
    }

    public void setParentCommits(ArrayList<String> parentCommits) {
        this.parents = parentCommits;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public void setBlobs(HashMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public void addParentID(String parentID) {
        this.parents.add(parentID);
    }

    public void saveCommitFile(String commitID) {
        File f = join(COMMIT_DIR, commitID);
        writeContents(f, this);
    }

    @Override
    public int compareTo(Commit c) {
        return this.timestamp.compareTo(c.timestamp);
    }
}
