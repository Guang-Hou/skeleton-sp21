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
1. `private static final File CWD` The Current Working Directory. It provides a way to access other files after adding the relevant relative path.
2. `private static final File GITLET_DIR` The hidden `.gitlet` directory. This is where all the persistence files will be stored.
3. `private static final File COMMITS_DIR` The .gitlet/commits directory to store serialized Commit objects.
4. `private static final File BLOBS_DIR` The .gitlet/blobs directory to store file blobs.
5. `private static final File STAGING_DIR` The .gitlet/stagingArea directory.
6. `private static File branchesFile` File storing the HashMap<String, String> of branchName : commitID. 
7. `private static File headFile` File storing the string of the head Commit ID.
8. `private static File activeBranchFile` File storing the string of the active branch name.
9. `private static File addFile` File storing HashMap<String, String> of fileName : fileHash for all files staged to add.
9. `private static File rmFile` File storing HashMap<String, String> of fileName : fileHash for all files staged to remove.
10. `private static HashMap<String, String> branches` HashMap<String, String> for branchName : commitID.
11. `private static String headID` Head Commit hash ID.
12. `private static String activeBranchName`  Active branch name in String.
13. `private static HashMap<String, String> addFileMap` HashMap<String, String> of fileName : fileHash for all files staged to add.
14. `private static HashMap<String, String> rmFileMap` HashMap<String, String> of fileName : fileHash for all files staged to remove.
15. `private static Commit headCommit` The head Commit object.This variable is not stored as file.


### Class 3: Commit
This class represents a `Commit` that will be stored in a file. Because each Commit will have a unique name (SHA hash code), 
we may simply use that as the name of the file that the object is serialized to.
#### Fields
1. `public String message` The message of this Commit.
2. `public Date timeStamp` The Date object representing the timestamp of a Commit.
3. `private ArrayList<String> parents` The parent commits of current commit, stored as String in an Array.
4. `public Map<String, String> blobs` The HashMap storing all the files in the format of fileName: hash.


## Algorithms

### Repository Class
The main logics reside in the **Repository class**.

1. `public static void init()` 
   1. Used for `java gitlet.Main init` command. 
   2. Create the necessary directories and make the initial commit.
   3. If there is already a Gitlet version-control system in the current directory, it will exit. 
2. `public static void checkInitialization()` 
   1. Check if .getlet directory is initialized. It should be called before any gitlet operations except init.
   2. Print a warning if CWD doesn't have `gitlet`system initialized.
3. `public static String saveObjectToSHA1Name(File destFolder, Serializable ob)`
   1. Save the Serializable object to the destination folder.
   2. Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
   3. Return the SHA1 hash.
4. `public static String copyFileToSHA1Name(File destFolder, File f)`
   1. Copy the file to the destination folder.
   2. Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
   3. Return the SHA1 hash.
5. `public static void readStaticVariables()`
   1. Read the static variables from their corresponding files.
   2. This is used before any function that requires these variables.
6. `public static void saveStaticVariableFiles()`
   1. Write the static variable files contents to the corresponding files.
7. `public static Commit readCommitFromFile(String commitID)`
   1. Read from the file in COMMITS_DIR to a Commit object.
   2. The file name is commitID in String.
8. `public static void printVariables()`
   1. Helper function for debugging.
   2. This prints the stabic variables contents.
9. `public static void add(String file)`
   1. Used for `java gitlet.Main add [file name]` command.
   2. Stage a file to be added in the repository. Add the file to blobs folder and put fileName:hash to the addFileMap.
   3. Update addFilemap
      1. case 1: If the headCommit blobs is null or the blobs doesn't have this file hash, add this fileName:hash entry to addFileMap.
      2. case 2: If headCommit blobs (fileName:hash) have this hash. 
         1. The existing fileBlob doesn't have this fileName, then put the newFileName:hash to teh addFilesMap.
         2. If the fileName exists in the headCommit fileBlobs.
            1. If the fileName:hash exists, no need to add. If it is in the addFileMap, remove it.
            2. If fileName: hash doesn't match, we need to update the hash in the addFileMap.
   4. Check if the file exists in the rmFileMap, if so remove it there.
   5. Save the static variables to files.
10. `public static void rmFile(String file)`
    1. Used for `java gitlet.Main rm [file name]` command.
    2. Stage a file to be removed from the repository.
    3. If the file name is in the `addFiles`, remove the file in the addFileMap list and remove the corresponding file.
    4. If the file is in the current commit's blobs, 
       1. Add the fileName : hash to the `rmFileMap. 
       2. If the fileDelete the file in CWD, delete it.
    5. If neither is the case, exit and print error message.
    6. Note that a file will not be both in the addFiles and current commit's blobs. See the add and makeCommit functions.
        1. After each commit, addFiles will be cleared.
        2. If the file is in the current commit's blobs, and we add this file again, it will not be staged in the addFiles.
11. `public static void makeCommit(String message)`
    1. Used for `java gitlet.Main commit [message]` command.
    2. Create a Commit object with the provided message. Save the Commit object to commits folder.
    3. Use the static instance variable `headCommit` create a new Commit object by the copying constructor.
    4. Update the Commit object instance variables based on the addFileMap and rmFileMap:
       1. Update message.
       2. Update fileBlobs.
          1. For each `file:hash` in the `addFiles`, update it in the `blobs` section of the commit.
          2. For each `file:hash` in the `rmFiles`, remove it from the `blobs` section of the commit.
       3. Save the commit object.
       4. Update headID and branch pointer.
    5. Clear staging area.
    6. Save static variables.
12. `public String showLocalLog()`
    1. Used for `java gitlet.Main log` command.
    2. Print the log history of the headCommit.
    3. Create a new StringBuilder object.
    4. Start from the headCommit, read the commitID, data and message, and its parent commitID.
    5. From the parent commitID, read its contents and add them to the StringBuilder. If there are multiple parentCommitIDs, use the first one.
    6. Repeat until reaching the initial commit where the parent commitID is null.
    7. Display the whole string content.
13. `public String showGlobalLog()`
     1. Used for `java gitlet.Main global-log` command.
     2. Show all commmits in no particular order.
     3. Iterate all the file names in commit folder.
     4. Read the files to Commit object into a string builder object.
14. `public static void showGlobalLogInOrder()`
    1. Show all commits in the order of timestamp.
    2. Implement the Commit class as Comparable and change the compareTo() based on timestamp.
    3. Use a TreeMap <Commmit, String> of Commit:ID and add all commit information. Then they will be in order.
    4. Iterate the TreeMap, it will return objects in natural order from smallest timestamp to largest timestamp.
15. `public String findCommitFromMessage(String message)`
    1. Used for `java gitlet.Main find [commit message]` command.
    2. Find all commit IDs having the provided message.
    3. Iterate all the file names in commit folder. Save commit ID where the Commit has the provided message.
16. `public String showStatus()`
    1. Used for `java gitlet.Main status` command.
    2. Print the repository information such as branches, staged files etc.
    3. From branchesMap, read all branch names.
    4. From `addFiles` and `rmFiles` read the list of file names to add and remove.
    5. Add modified files but not staged for commit.
    6. Add untracked file names.
17. `public void checkoutCommitAndFile(String commitID, String fileName)`
    1. Used for `java gitlet.Main checkout [commit id] -- [file name]` command.
    2. Change the file's contents according to its snapshot in the provided commitID.
    3. Use the commitID to read the relevant commit object.
    4. From the commit object, get the file's hash.
    5. Use the hash, get the serialized file from the blobs folder, copy it to the CWD and overwrite the file if it is already there.
18. `public void checkoutFile(String fileName)`
    1. Used for `java gitlet.Main checkout -- [file name]` command.
    2. Change a file's content according to the headCommit.
    3. Call checkoutCommitAndFile(headID, fileName)
19. `public void checkoutBranch(String branchName)`
    1. Used for `java gitlet.Main checkout [branch name]` command.
    2. Change the CWD contents according to the branch's latest commit.
    3. Use the branch name to get the branch latest commitID from branchesMap.
    4. Call resetCommitFiles to copy files to CWD.
    5. Point the `head` file to this commit. Change the activeBranch to the branchName.
    6. Clear the stagingArea.
20. `public void createBranch(String branchName)`
    1. Used for `java gitlet.Main branch [branch name]` command.
    2. Create a new branch and point it to the headCommit.
    3. Update the branchesMap, add the branchName : headID
21. `public void rmBranch(String branchName)`
    1. Used for `java gitlet.Main rm-branch [branch name]` command.
    2. Remove the provided branch from the repository.
    3. Update the `branches` file, delete the branchName entry.
22. `public void resetCommit(String preCommitID)`
    1. Used for `java gitlet.Main reset [commit id]` command.
    2. Change CWD contents according to the provided commit id. Update head pointer and branch pointer.
    3. Call helper function resetCommitFiles().
    4. Change the head pointer and branch pointer to preCommitID.
    5. Clear the stagingArea.
23. `public void resetCommitFiles(String preCommitID)`
    1. Helper function to change CWD contens based on the provided commit id.
    2. Read the commit object with the id of preCommitID.
    3. Check if the commit file exists and if there is any untracked files in CWD.
    4. Delete all files in CWD.
    5. Copy files from the preCommit blobs to CWD.
24. `public void merge(String branch)`
    1. Used for `java gitlet.Main merge [branch name]` command.
    2. Merges files from the given branch into the current branch.
    3. Create a new Commit object by copying the head from the current branch. Use it as the baseline and modify it. 
    4. We only need to incorporate the given branch's changes of the ancestor to the current branch.
       1. For the files that the given branch just inherits from the ancestor but did not modify. No action is needed. Since we are using the current branch as baseline.
       2. For the changes the given branch brings to the common ancestor
          1. If the given branch deletes files from the common ancestor
             1. if these files are absent in the current branch, no action is needed
             2. if these files are present in the current branch, 
                1. if they are not modified by the current branch, then we need to delete them from the current branch.
                2. if they are modified in the current branch, we need to keep them, no action is needed.
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
                   we have a merge conflict.
       3. For the common files between the given branch and the current branch, if they both modified the file from the common ancestor, we have a conflict
           1. if they have different hash, but only one branch modified the common ancestor, we do not have a conflict
    5. Steps
       1. Call helper function to find the split point, the latest common ancestor.
       2. If there are actually no branches in the tree:
          1. If the split point is the same commit as the given branch, do nothing and return
          2. If the split point is the current branch, then the effect is to check out the given branch
       3. Handle case 3.2.1: the given branch deleted files
          1. Set operation to filter the files in the common ancestor but not in the given branch
          2. Set operation to filter the files that are present in the current branch
          3. Find the files that have the same hash in current branch as the hash in the ancestor, they should be staged for removal (and untracked)
       4. Handle case 3.2.2: the given branch added new files
          1. find the files in the given branch but not in the ancestor
          2. filter the files that are not present in the current branch, they should be staged for add
          3. filter the files that are present in the current branch, and if they have different hash code between given branch and current branch, call handleConflict helper function.
       5. Handle case 3.2.3: the given branch modified files from ancestor
          1. find files that are both in given branch and ancestor but they have different hash 
          2. filter the files that are absent in the current branch, add them (to stagingArea? or copy to CWD?)
          3. filter the files that are present in the current branch
              1. if these files in current branch have different hash in the given branch
                 1. If in the current branch, those files are the same as the ancestor, stage them for add.
                 2. If in the current branch, those files are the different from the ancestor, and different from the current branch, call handleConflict helper function.
       6. At the end, make a new commit
25. `public Commit findLatestCommonAncestor(String branch1, String branch2)`
    1. Helper function for merge
    2. Find the latest common ancestor for two branches
       1. use two pointers, switch position when reaching the end.
       2. when they are equal, we found the ancestor.
26. `public void handleConflict(String file1, String file2) `
    1. Helper function to handle merge conflicts
27. `public Set<String> toSet(Commit c)` 
    1. Helper function to put the file names in the commit to a set

These functions can be replaced with the standard set operations, since we can convert the commit file list to a set.
22. `public Set<String> commonFiles(Commit c1, Commit c2)`
    1. Helper function to find the files with the same name in c1 and c2
23. `public Set<String> commonFiles(Set<String> s1, Commit c2)`
    1. Helper function to find the files with the same name in s1 and c2
24. `public Set<String> differentFiles(Commit c1, Commit c2)`
    1. Helper function to find the files that are in c1 but not c2
25. `public Set<String> differentFiles(Set<String> s1, Commit c2)`
    1. Helper function to find the files that are in s1 but not c2

### Commit Class
The **Commit class** provides the way to represent Commit information.
1. `public Commit()` The default constructor, used for the initial commit.
2. `public Commit(Commit parent)` Constructor based on an existing Commit. Copy everything but add the current timestamp.
3. `public Commit(String parentCommitID, Commit parentCommit)` The copy constructor which will copy most contents from parentCommit. It will set the timestamp as the time of commit.
4. `public String toString()` This is just for debugging purpose.
5. `public String toString(String id)` This is used in other classes for printing the Commit object.
6. `public String getMessage()` Getter method for instance variable message.
7. `public void setMessage(String message)` Setter method for instance variable message.
8. `public ArrayList<String> getParentCommits()` Getter method for instance variable parents.
9. `public HashMap<String, String> getBlobs()` Getter method for instance variable blobs.
10. `public void setBlobs(HashMap<String, String> blobs)` Setter method for instance variable blobs.
11. `public void addParentID(String parentID)` Add the parentID to its instance variable parents of ArrayList<parentID>.
12. `public int compareTo(Commit c)` Implementation of Comparable.


## Persistence
The directory structure looks like this:
```
CWD                             <==== The current working directory is.
└── .gitlet                     <==== All persistant data is stored within this directory.
    ├── stagingArea             <==== Directory for the stagingArea files.
        ├── ADD                 <==== A serialized hashmap<String, String> stores fileName : hashID for files staged to add.
        ├── RM                  <==== A serialized hashmap<String, String> stores fileName : hashID for files staged to remove.
    ├── BRANCHES                <==== A serialized hashmap<String, String> stores branchName : hash of the branch's latest commit.
    ├── ACTIVE_BRANCH           <==== File storing the string of the active branch name.  
    ├── HEAD                    <==== A serialized String storing the name of the current branch.
    └── commits                 <==== Directory for all serialized Commits objects.
        ├── Commit1             <==== A single Commit instance stored to a file.
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

The commits folder stores the serialized commit objects.
- Each Commit object has the following instance variables:
   1. message
   2. timeStamp
   3. parentCommit
   4. List of file name: blob hash code
- Each Commit object name is the SHA1 hash code of the Commit object.