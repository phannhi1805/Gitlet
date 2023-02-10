package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;



/** Represents a gitlet repository.

 *  @author Nhi Phan
 */
public class Repository implements Serializable {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directories.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA = join(GITLET_DIR, "Staging Area");
    public static final File SA = join(STAGING_AREA, "Staging for Addition");
    public static final File SR = join(STAGING_AREA, "Staging for Removal");

    /** K for branch name, V for the most current commit id of that branch */
    private HashMap<String, String> branches;

    /** current branch */
    private String head;


    /** Constructor - called by init */
    public Repository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            STAGING_AREA.mkdir();
            SA.mkdir();
            SR.mkdir();
            Commit.COMMIT_FOLDER.mkdir();
            Commit init = new Commit();
            String initID = init.getID();
            Commit.saveCommit(init);
            head = init.getBranch();
            branches = new HashMap<>();
            branches.put(head, initID);
        } else {
            message("A Gitlet version-control system already exists "
                    + "in the current directory.");
            throw new GitletException();
        }
    }


    public void add(String fileName) {
        File fileCWD = join(CWD, fileName);
        if (!fileCWD.exists()) {
            message("File does not exist.");
            throw new GitletException();
        }

        Blob blob = new Blob(fileName);
        String blobID = blob.getID();
        Commit headCommit = headCommit();
        HashMap<String, Blob> trackedBlobs = headCommit.getBlobs();

        File sameName = join(SA, fileName);
        if (sameName.exists()) {
            restrictedDelete(sameName);
            if ((trackedBlobs == null) || (!trackedBlobs.containsKey(blobID))) {
                blob.saveBlob(SA);
            }
        } else {
            blob.saveBlob(SA);
        }
    }

    public void commit(String message) {
        if (message.equals("")) {
            message("Please enter a commit message.");
            throw new GitletException();
        }
        Commit headCommit = headCommit();
        HashMap<String, Blob> trackedBlobs = headCommit.getBlobs();
        if (trackedBlobs == null) {
            trackedBlobs = new HashMap<>();
        }
        if ((SA.list() != null) || (SR.list() != null)) {
            for (String fileName : Objects.requireNonNull(SA.list())) {
                File file = join(SA, fileName);
                Blob needed = readObject(file, Blob.class);
                trackedBlobs.put(fileName, needed);
                file.delete();
            }
            for (String fileName : Objects.requireNonNull(SR.list())) {
                trackedBlobs.remove(fileName);
                File file = join(SA, fileName);
                file.delete();
            }
        } else {
            message("No changes added to the commit");
            throw new GitletException();
        }

        String parent1 = branches.get(head);
        Commit newCommit = new Commit(message, parent1, null, trackedBlobs, head);
        Commit.saveCommit(newCommit);
        branches.put(head, newCommit.getID());



        System.out.println(newCommit.getID());
        System.out.println(newCommit.getParent1());
        System.out.println(newCommit.getBranch());
        System.out.println(head);
        System.out.println(parent1);
        System.out.println(branches.get(head));
    }

    public void rm(String fileName) {
        boolean staged = false;
        File sameName = join(SA, fileName);
        if (sameName.exists()) {
            sameName.delete();
            staged = true;
        }

        boolean tracked = false;
        Blob blob = new Blob(fileName);
        Commit headCommit = headCommit();
        HashMap<String, Blob> trackedBlobs = headCommit.getBlobs();
        if (trackedBlobs != null) {
            for (String name : trackedBlobs.keySet()) {
                if (name.equals(fileName)) {
                    tracked = true;
                    blob.saveBlob(SR);
                    File fileCWD = join(CWD, fileName);
                    fileCWD.delete();
                }
            }
        }
        if (!staged || !tracked) {
            message("No reason to remove the file");
            throw new GitletException();
        }
    }

    public void checkout(String id, String fileName) {
        String fullID = getLongID(id);
        Commit checking = idToCommit(fullID);
        HashMap<String, Blob> trackedBlobs = checking.getBlobs();
        if (!trackedBlobs.containsKey(fileName)) {
            message("File does not exist in that commit.");
            throw new GitletException();
        }
        Blob file = trackedBlobs.get(fileName);
        File fileInCWD = join(CWD, fileName);
        if (fileInCWD.exists()) {
            restrictedDelete(fileInCWD);
        }
        File fileNow = join(CWD, fileName);
        String content = file.getContentsAsString();
        writeContents(fileNow, content);
    }

    public void checkout(String fileName) {
        String id = branches.get(head);
        checkout(id, fileName);
    }

    public void checkoutBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            message("No such branch exists.");
            throw new GitletException();
        }
        if (head.equals(branchName)) {
            throw new GitletException("No need to checkout the current branch.");
        }
        checkUntrackedFiles(CWD);


        String commitID = branches.get(branchName);
        Commit current = idToCommit(commitID);
        HashMap<String, Blob> trackedBlobs = current.getBlobs();

        if (trackedBlobs != null) {
            for (Blob blob : trackedBlobs.values()) {
                String fileName = blob.getName();
                String blobID = blob.getID();
                checkout(blobID, fileName);
            }
        }

        head = branchName;
        cleanFile(SA);
        cleanFile(SR);
    }

    public void log() {
        String headID = branches.get(head);
        while (headID != null) {
            Commit current = idToCommit(headID);
            System.out.println("===");
            System.out.println("commit " + headID);
            System.out.println("Date: " + current.getTimestamp());
            System.out.println(current.getMessage());
            System.out.println();
            headID = current.getParent1();
        }
    }

    public void globalLog()  {
        for (File f : Objects.requireNonNull(Commit.COMMIT_FOLDER.listFiles())) {
            String commitID = f.getName();
            Commit current = idToCommit(commitID);
            System.out.println("===");
            System.out.println("commit " + commitID);
            System.out.println("Date: " + current.getTimestamp());
            System.out.println(current.getMessage());
            System.out.println();
        }
    }

    public void find(String message) {
        boolean found = false;
        for (File f : Objects.requireNonNull(Commit.COMMIT_FOLDER.listFiles())) {
            String commitID = f.getName();
            Commit c = idToCommit(commitID);
            if (c.getMessage().equals(message)) {
                System.out.println(c.getID());
                found = true;
            }
        }

        if (!found) {
            message("Found no commit with that message.");
            throw new GitletException();
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        Object[] keys = branches.keySet().toArray();
        Arrays.sort(keys);
        for (Object branch : keys) {
            if (branch.equals(head)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String fileName : Objects.requireNonNull(plainFilenamesIn(SA))) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName : Objects.requireNonNull(plainFilenamesIn(SR))) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void branch(String branchName) {
        if (branches.containsKey(branchName)) {
            message("A branch with that name already exists.");
            throw new GitletException();
        } else {
            String currentID = branches.get(head);
            branches.put(branchName, currentID);
        }
    }

    public void rmBranch(String branchName) {
        if (head.equals(branchName)) {
            message("Cannot remove the current branch.");
            throw new GitletException();
        } else if (!branches.containsKey(branchName)) {
            message("A branch with that name already exists.");
            throw new GitletException();
        } else {
            branches.remove(branchName);
        }
    }

    public void reset(String commitID) {
        commitID = getLongID(commitID);
        Commit wanted = idToCommit(commitID);
        HashMap<String, Blob> trackedBlobs = wanted.getBlobs();
        checkUntrackedFiles(CWD);

        for (Blob blob : trackedBlobs.values()) {
            String blobID = blob.getID();
            String fileName = blob.getName();
            checkout(blobID, fileName);
        }

        branches.put(head, commitID);
        cleanFile(SA);
        cleanFile(SR);
    }






    /** Helper functions */
    private Commit headCommit() {
        String currentID = branches.get(head);
        File file = join(Commit.COMMIT_FOLDER, currentID);
        return readObject(file, Commit.class);
    }

    private String getLongID(String shortID) {
        if (shortID.length() == UID_LENGTH) {
            return shortID;
        }

        File[] commits = Commit.COMMIT_FOLDER.listFiles();
        for (File file : Objects.requireNonNull(commits)) {
            String fileID = file.getName();
            if (fileID.substring(0, 6).equals(shortID)) {
                return file.getName();
            }
        }
        message("No commit with that id exists");
        throw new GitletException();
    }

    private void checkUntrackedFiles (File f) {
        String s = "There is an untracked file in the way; delete it, or add and commit it first.";
        Commit headCommit = headCommit();
        HashMap<String, Blob> trackedBlobs = headCommit.getBlobs();
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isDirectory()) {
                continue;
            }
            if ((file.getName().equals("gitlet-design.md"))
                    || (file.getName().equals("Makefile"))
                    || (file.getName().equals("pom.xml"))
                    || (file.getName().equals("proj2.iml"))
                    || (file.getName().equals(".gitlet"))) {
                continue;
            }
            if ((trackedBlobs == null) && (Objects.requireNonNull(f.listFiles()).length > 1)) {
                message(s);
                throw new GitletException();
            } else {
                boolean notTracked = false;
                File add = join(SA, file.getName());
                File rev = join(SR, file.getName());
                if (!add.exists() && !rev.exists()) {
                    notTracked = true;
                }
                if (!trackedBlobs.containsKey(file.getName())) {
                    notTracked = true;
                }
                if (notTracked) {
                    message(s);
                    throw new GitletException();
                }
            }
        }
    }

    private Commit idToCommit(String id) {
        File file = join(Commit.COMMIT_FOLDER, id);
        return readObject(file, Commit.class);
    }

    private void cleanFile(File f) {
        for (File file : Objects.requireNonNull(f.listFiles())) {
            restrictedDelete(file);
        }
    }








}
