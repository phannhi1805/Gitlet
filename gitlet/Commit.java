package gitlet;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Commit implements Serializable {
    /** Commit directories*/
    public static final File COMMIT_FOLDER = join(Repository.GITLET_DIR, "Commit Folder");
    /**
     * The message of this Commit.
     */
    private String message;

    /**
     * Metadata.
     */
    private String id;
    private String timestamp;
    /** K is name, V is blob */
    private HashMap<String, Blob> blobs;
    private String parent1;
    private String parent2;
    private String branch;

    /** Constructors */
    public Commit(String m, String p1, String p2, HashMap<String, Blob> b, String br) {
        timestamp = makeTimestamp();
        message = m;
        parent1 = p1;
        parent2 = p2;
        blobs = b;
        branch = br;
        id = makeID();
    }

    /** Constructor dedicated for init() */
    public Commit() {
        message = "initial commit";
        Date date = new Date(0);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        timestamp = formatter.format(date);
        id = Utils.sha1(timestamp);
        parent1 = null;
        parent2 = null;
        blobs = null;
        branch = "master";
    }


    /** All the get methods to access Commit instant vars */
    public String getID() {
        return this.id;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String getParent1() {
        return this.parent1;
    }

    public String getParent2() {
        return this.parent2;
    }

    public HashMap<String, Blob> getBlobs() {
        return this.blobs;
    }

    public String getBranch() {
        return this.branch;
    }

    /** Formatting current time */
    public String makeTimestamp() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return formatter.format(date);
    }

    /**Formatting id */
    public String makeID() {
        String blobsString = blobs.toString();
        String obj = blobsString;
        return Utils.sha1(obj);
    }


    public static void saveCommit(Commit c) {
        String id = c.getID();
        File commitFile = join(COMMIT_FOLDER, id);
        writeObject(commitFile, c);
    }
}
