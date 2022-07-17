# Gitlet Design Document

**Name**: Guang Hou

## Classes and Data Structures

### Class 1: Main
This is the entry point to our program. 
It takes in arguments from the command line and based on the command (the first element of the args array) 
calls the corresponding command in Repository class which will actually execute the logic of the command. 
It also validates the arguments based on the command to ensure that enough arguments were passed in.

#### Fields
This class has no fields and hence no associated state: it simply validates arguments and defers the execution to the Repository class.


### Class 2: Repository
This class will handle all input commands by reading/writing from/to the correct files, setting up persistence, and additional error checking.

#### Fields
1. `public static final File CWD` The Current Working Directory. It provides a way to access other files after adding the relevant relative path.
2. `public static final File GITLET_DIR` The hidden `.gitlet` directory. This is where all the persistence files will be stored.
3. `public static final File HEAD` The `head` file object which stores the current active commit and active branch. It stores the information in strings. 
4. `public static Commit headCommit` Static variable of the Head commit object 


### Class 3: Commit
This class represents a `Commit` that will be stored in a file. Because each Commit will have a unique name (SHA hash code), 
we may simply use that as the name of the file that the object is serialized to.

#### Fields
1. `public String message` The commit message.
2. `public String timeStamp` The timeStamp.
3. `public String parentCommitID` The previous parent commitID.
4. `public String branch` The branch.
5. `public Map<String, String> blobs` A hashmap storing the file names and their hash codes.


## Algorithms
The main logics reside in the **Repository class**. 

1. `public static void init()` 
   1. Used for `java gitlet.Main init` command. 
   2. It will set up the folders and create the initial commit
   3. If there is already a Gitlet version-control system in the current directory, it should abort. 
   It should NOT overwrite the existing system with a new one. Should print the error message `A Gitlet version-control system already exists in the current directory.`
2. `public static void checkInitialization()` 
   1. Check if there is an initialized `gitlet` folder. 
   2. Print a warning if CWD doesn't have `gitlet`system initialized.
3. `public static void add(String file)`
   1. Used for `java gitlet.Main add [file name]` command.
   2. Create a SHA1 hash of the file content.
   3. Check if this SHA1 exists in the current Commit's blobs hashmap.
      1. if it exists, then do not stage it. Remove it from the staging area if it already there.
      2. if it doesn't exist, add the fileName : hash the stagingArea `addFiles`. 
      3. Serialize the file to the `blobs` folder, the file name should be its hash.
   4. The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
      1. check if the file is in the StagingArea `rmFiles`, if so remove it there
4. `public static void makeCommit(String message)`
   1. Used for `java gitlet.Main commit [message]` command.
   2. Use the static instance variable `head`, and create a new Commit object by copying constructor.
   3. Check if the two files in stagingArea: `addFiles` and `rmFiles`, 
      1. if empty print out message and exit.
      2. If there are files in the stagingArea. Update them in the commit.
   4. update the commit.
      1. update message.
      2. For each `file:hash` in the `addFiles`, update it in the `blobs` section of the commit.
      3. For each `file:hash` in the `rmFiles`, remove it from the `blobs` section of the commit.
   5. save commit to a serialized file to the commits folder.
   6. change `head` content as the new commit hash and write it to update the file.
5. `public static void rmFile(String file)`
   1. Used for `java gitlet.Main rm [file name]` command
   2. check if the file name is in the `addFiles`
      1. if true, remove the file in the `addFiles` list and remove the corresponding file
      2. if false, check if the file is in the current commit's blobs
         1. if true, add the fileName : hash to the `rmFiles`
6. `public String showLocalLog()`
   1. Used for `java gitlet.Main log` command
   2. Create a new StringBuilder object
   3. Start from the `head` file, read the commitID, data and message, and its parent commitID.
   4. From the parent commitID, read its contents and add them to the StringBuilder.
   5. Repeat until reaching the initial commit where the parent commitID is null.
   6. Display the whole string content.
7. `public BST<STring> createCommitBST()`
   1. Helper function to build a BST tree of all commits, ordered by the commit date.
   2. Iterate through all the commit objects under `commits` folder, add them to the BST.
8. `public String showGlobalLog()`
    1. Used for `java gitlet.Main global-log` command
    2. Call the helper function `createCommitBST()` to build a BST tree
    3. InOrderTraversal of the tree. Build a string by adding the commit contents.
9. `public String findCommitFromMessage(String message)`
   1. Used for `java gitlet.Main find [commit message]` command
   2. Call helper function `createCommitBST()` to build a BST tree
   3. InOrderTraversal of the tree. If the commit message is relevant, add all the commit contents to the string.
10. `public String showStatus()`
    1. Used for `java gitlet.Main status` command
    2. From `head` read the current commit branch information.
    3. From `addFiles` and `rmFiles` read the list of file names to add and remove.
11. `public void checkoutFile(String file)`
    1. Used for `java gitlet.Main checkout -- [file name]` command
    2. From the `headCommit` static variable, get the file's hash
    3. Use the hash, get the serialized file from the blobs folder, copy it to the CWD and overwrite the file if it is already there.
12. `public void checkoutCommitAndFile(String commitID, String file)`
    1. Used for `java gitlet.Main checkout [commit id] -- [file name]` command
    2. Use the commitID to read the relevant commit object, 
    3. From the commit object, get the file's hash
    4. Use the hash, get the serialized file from the blobs folder, copy it to the CWD and overwrite the file if it is already there.
13. `public void checkoutBranch(String branch)`
    1. Used for `java gitlet.Main checkout [branch name]` command
    2. Use the branch name to search the `branches` hash file, read the head of that branch
    3. Use the head search the commits folder to get the relevant object
    4. From the commit, get the list of files' hashes
    5. Use the hashes, get the serialized file from the blobs folder, copy it to the CWD and overwrite the file if it is already there.
    6. Point the `head` file to this commit.
    7. Clear the stagingArea.
14. `public void createBranch(String branch)`
    1. Used for `java gitlet.Main branch [branch name]` command
    2. Update the `branches` file, add the branch name : current commitID
15. `public void rmBranch(String branch)`
    1. Used for `java gitlet.Main rm-branch [branch name]` command
    2. Update the `branches` file, delete the branch name : current commitID
16. `public void resetCommit(String commitID)`
    1. Used for `java gitlet.Main reset [commit id]` command
    2. Read the commit object
    3. call `public void checkoutFile(String file)` on all the files in the commit object
    4. Change the current branch content to the input commitID
    5. Clear the stagingArea
17. `public void merge(String branch)`
    1. Used for `java gitlet.Main merge [branch name]` command
    2. Create a new Commit object by copying the head from the current branch. Use it as the baseline and modify it. 
    3. We only need to consider the given branch's impact on the current branch and on the split node.
       1. For the common files between the given branch and the current branch, if they both modified the file from the common ancestor, we have a conflict
          1. if they have different hash, but only one branch modified the common ancestor, we do not have a conflict
       2. For the changes the given branch brings to the common ancestor
          1. If the given branch deletes files from the common ancestor
             1. if these files are absent in the current branch, no action is needed
             2. if these files are present in the current branch, and if they are not modified by the current branch, then we need to delete them from the current branch. 
                If they are modified in the current branch, we need to keep them, no action is needed.
          2. If the given branch added new files to the common ancestor.
             1. If these files are inside the current branch.
                1. if they have same hashes, no action is needed.
                2. if they have different hashes, we have a conflict.
             2. If they are not present in the current branch, we need to add these changes to the current branch.
          3. If the given branch modified files from the common ancestor.
             1. If these changed files are absent in the current branch, we want to add them
             2. If these changed files are present in the current branch
                1. If in the current branch, those files are the same as the given branch, no action is needed.
                2. If in the current branch, those files are the same as the ancestor, we want to keep the changed version from the given branch.
                3. If in the current branch, those files are the different from the ancestor, and different from the current branch,
                   we have a conflict.
    4. Steps
       1. Call helper function to find the split point, the latest common ancestor.
       2. If there are actually no branches in the tree:
          1. If the split point is the same commit as the given branch, do nothing and return
          2. If the split point is the current branch, then the effect is to check out the given branch
       3. Handle case 3.2.1: given branch deletes files
          1. find the files in the common ancestor but not in the given branch
          2. filter the files that are present in the current branch
          3. filter the files that have the same hash as the hash in the ancestor, they should be removed (and untracked)
       4. Handle case 3.2.2: given branch added new files
          1. find the files in the given branch but not in the ancestor
          2. filter the files that are not present in the current branch, add them
          3. filter the files that are present in the current branch, and if they have different hash code between given branch and current branch, call handleConflict helper function.
       5. Handle case 3.2.3: given branch modified files from ancestor
          1. find files that are both in given branch and ancestor but they have different hash 
          2. filter the files that are absent in the current branch, add them
          3. filter the files that are present in the current branch
              1. if these files in current branch have different hash in the given branch
                 1. If in the current branch, those files are the same as the ancestor, add the given branch version to the commit.
                    1. If in the current branch, those files are the different from the ancestor, and different from the current branch, call handleConflict helper function.
18. `public Commit findLatestCommonAncestor(String branch1, String branch2)`
    1. Helper function for merge
    2. Find the latest common ancestor for two branches
       1. use two pointers, switch position when reaching the end.
       2. when they are equal, we found the ancestor.
19. `public void handleConflict(String file1, String file2) `
    1. Helper function to handle merge conflicts
20. `public Set<String> commonFiles(Commit c1, Commit c2)`
    1. Helper function to find the files with the same name in c1 and c2
20. `public Set<String> commonFiles(Set<String> s1, Commit c2)`
    1. Helper function to find the files with the same name in s1 and c2
21. `public Set<String> differentFiles(Commit c1, Commit c2)`
    1. Helper function to find the files that are in c1 but not c2
21. `public Set<String> differentFiles(Set<String> s1, Commit c2)`
    1. Helper function to find the files that are in s1 but not c2

The **Commit class** handles commit related operations.
1. `public Commit(Commit parent)` Constructor based on an existing Commit. Copy everything but add the current timestamp.
2. `public static Commit fromFile(String name)`
3. `public String toString()`


## Persistence

The directory structure looks like this:

```
CWD                             <==== Whatever the current working directory is.
└── .gitlet                     <==== All persistant data is stored within this directory
    ├── stagingArea             <==== Directory for the stagingArea files
        ├── addFiles            <==== A serialized hashmap<String, String> stores fileName : hash for filed staged to add
        ├── rmFiles             <==== A serialized hashmap<String, String> stores fileName : hash for filed staged to remove 
    ├── branches                <==== A serialized hashmap<String, String> stores branchName : hash of the branch's latest commit                
    ├── head                    <==== A serialized String storing the hash of the current active commit
    └── commits                 <==== Directory for all serialized Commits objects
        ├── Commit1             <==== A single Commit instance stored to a file
        ├── Commit2
        ├── ...
        └── CommitN
    └── blobs                   <==== Directory for all serialized file blobs
        ├── Blob1               <==== A single Blob file storing the serialized file
        ├── Blob2
        ├── ...
        └── BlobN
```

The blobs folder stores the serialized file contents. 
1. Each Blob corresponds to a version of a file.
2. The Blob file name is the SHA hash code of the file.

The commits folder stores the commit information.
1. Each Commit object has the following instance variables:
   1. message
   2. timeStamp
   3. parentCommit
   4. List of file name: blob hash code
2. Each Commit object name is the SHA hash code of the Commit object.