package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides the way to represent Commit information.
 *
 * @author Guang Hou
 */
public class Commit implements Serializable, Comparable<Commit> {

    /* The message of this Commit. */
    private String message;
    /* The Date object representing the timestamp of a Commit. */
    private Date timestamp;
    /* The parent commits of current commit, stored as String in an Array. */
    private ArrayList<String> parentIDs = new ArrayList<>();
    /* The HashMap storing all the files in the format of fileName: hash. */
    private HashMap<String, String> blobs = new HashMap<>();

    /**
     * The default constructor, used for the initial commit.
     */
    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parentIDs = null;
        blobs = null;
    }

    /**
     * The copy constructor which will copy most contents from parentCommit.
     * It will set the timestamp as the time of commit.
     *
     * @param parentCommitID The hash id of parent Commit.
     * @param parentCommit   The parent Commit object.
     */
    public Commit(String parentCommitID, Commit parentCommit) {
        timestamp = new Date();
        parentIDs.clear();
        parentIDs.add(parentCommitID);
        Map<String, String> parentBlobs = parentCommit.getBlobs();
        if (parentBlobs != null) {
            blobs = new HashMap<String, String>(parentBlobs);
        }
    }

    /**
     * This is just for debugging purpose.
     */
    @Override
    public String toString() {
        return "Date - " + timestamp + "\n"
                + "message - " + message + "\n"
                + "parents - " + parentIDs + "\n"
                + "blobs - " + blobs;
    }

    /**
     * This is used in other classes for printing the Commit object.
     */
    public String toString(String id) {
        String merge = "";

        if (parentIDs != null && parentIDs.size() > 1) {
            merge += "Merge: ";
            for (String parent : parentIDs) {
                merge += parent.substring(0, 7) + " ";
            }
            merge += "\n";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        String stringDate = dateFormat.format(timestamp);

        return "===" + "\n"
                + "commit " + id + "\n"
                + merge
                + "Date: " + stringDate + "\n"
                + message + "\n";
    }

    /**
     * Getter method for instance variable message.
     *
     * @return this.message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for instance variable message.
     *
     * @param message The user provided message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter method for instance variable parents.
     *
     * @return The parents in ArrayList<parentCommitID>.
     */
    public ArrayList<String> getParentCommitIDs() {
        return parentIDs;
    }

    /**
     * Getter method for instance variable blobs.
     *
     * @return The file blobs in HashMap<fileName, fileHash>.
     */
    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    /**
     * Setter method for instance variable blobs.
     *
     * @param blobs HashMap<fileName, fileHash>.
     */
    public void setBlobs(HashMap<String, String> blobs) {
        this.blobs = blobs;
    }

    /**
     * Add the parentID to its instance variable parents of ArrayList<parentID>.
     *
     * @param parentID The parentID.
     */
    public void addParentID(String parentID) {
        this.parentIDs.add(parentID);
    }

    /**
     * Implementation of Comparable.
     *
     * @param c The other Commit object.
     * @return Comaprion result in int.
     */
    @Override
    public int compareTo(Commit c) {
        return this.timestamp.compareTo(c.timestamp);
    }
}
