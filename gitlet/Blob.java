
package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;

import static gitlet.Utils.*;

/** Class Blob for Gitlet.
 */
public class Blob implements Serializable {

    private String blobName;
    private String blobID;
    private byte[] blobContent;
    private String blobContentAsString;
    private String blobTimestamp;

    /**
     * Constructor
     */
    public Blob(String name) {
        File file = new File(name);
        blobName = name;
        blobContent = readContents(file);
        blobContentAsString = readContentsAsString(file);
        blobTimestamp = makeTimestamp();
        blobID = makeId();
    }

    /**
     * Create the unique hashID for the Blob object
     */
    private String makeId() {
        return Utils.sha1(blobContentAsString);
    }

    /** All the get methods */
    public String getName() {
        return blobName;
    }

    public String getID() {
        return blobID;
    }

    public byte[] getContents() {
        return blobContent;
    }

    public String getContentsAsString() {
        return blobContentAsString;
    }

    public String makeTimestamp() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return formatter.format(date);
    }

    public void saveBlob(File f) {
        String name = this.getName();
        File commitFile = join(f, name);
        writeObject(commitFile, this);
    }

}
