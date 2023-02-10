package gitlet;

import java.io.File;
import static gitlet.Utils.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> .... */

    public static void main(String... args) {
        Repository repo = null;

        if (args.length == 0) {
            message("Please enter a command.");
            throw new GitletException();
        }

        File currentRepo = join(Repository.GITLET_DIR, "Current");

        // Save repo
        if (Repository.GITLET_DIR.exists()) {
            if (currentRepo.exists()) {
                repo= readObject(currentRepo, Repository.class);
            }
        }


        String command = args[0];
        switch (command) {
            case "init":
                if (!repo.GITLET_DIR.exists()) {
                    validateNumArgs(args, 1);
                    repo = new Repository();
                    writeObject(currentRepo, repo);
                }
                break;
            case "add":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.add(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "commit":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.commit(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "status":
                validateInit(repo);
                validateNumArgs(args, 1);
                repo.status();
                writeObject(currentRepo, repo);
                break;
            case "rm":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.rm(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "log":
                validateNumArgs(args, 1);
                repo.log();
                writeObject(currentRepo, repo);
                break;
            case "global-log":
                validateNumArgs(args, 1);
                repo.globalLog();
                writeObject(currentRepo, repo);
                break;
            case "find":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.find(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "branch":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.branch(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "rm-branch":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.rmBranch(args[1]);
                writeObject(currentRepo, repo);
                break;
            case "checkout":
                validateInit(repo);
                if (args[1].equals("--")) {
                    validateNumArgs(args, 3);
                    repo.checkout(args[2]);
                    writeObject(currentRepo, repo);
                    break;
                } else if (args[2].equals("==")) {
                    validateNumArgs(args, 4);
                    repo.checkout(args[1], args[3]);
                    writeObject(currentRepo, repo);
                    break;
                } else {
                    validateNumArgs(args, 2);
                    repo.checkoutBranch(args[1]);
                    writeObject(currentRepo, repo);
                    break;
                }
            case "reset":
                validateInit(repo);
                validateNumArgs(args, 2);
                repo.reset(args[1]);
                writeObject(currentRepo, repo);
                break;
            default:
                message("No command with that name exists.");
                throw new GitletException();
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if(args.length != n) {
            message("Incorrect operands.");
            throw new GitletException();
        }
    }

    public static void validateInit(Repository repo) {
        if (!repo.GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            throw new GitletException();
        }
    }

}
