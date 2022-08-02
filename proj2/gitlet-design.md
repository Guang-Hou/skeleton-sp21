# Gitlet Design Document

**Name**: Guang Hou

## Classes and Data Structures
### Class 1: Main
This is the entry point to our program. 
It takes in arguments from the command line and based on the command it calls the corresponding methods in the Repository class which will actually execute the logic of the command. 
It also validates the arguments to ensure that enough arguments were passed in.
#### Fields
This class has no fields and hence no associated state: it simply validates arguments and defers the execution to the Repository class methods.


### Class 2: Repository
This class will handle all user input commands with class methods. The methods operate by reading/writing from/to the correct files, setting up persistence, and additional error checking.
#### Fields
1. `private static final File CWD` The Current Working Directory. It provides a way to access other files after adding the relevant relative path.
2. `private static final File GITLET_DIR` The hidden `.gitlet` directory. This is where all the persistence files will be stored.
3. `private static final File COMMITS_DIR` The .gitlet/commits directory to store serialized Commit objects.
4. `private static final File BLOBS_DIR` The .gitlet/blobs directory to store file blobs.
5. `private static File branchesFile` File storing the HashMap<String, String> of branchName : commitID. 
6. `private static File headFile` File storing the string of the head Commit ID.
7. `private static File activeBranchFile` File storing the string of the active branch name.
8. `private static File addFile` File storing HashMap<String, String> of fileName : fileHash for all files staged to add.
9. `private static File rmFile` File storing HashMap<String, String> of fileName : fileHash for all files staged to remove.
10. `private static HashMap<String, String> branches` HashMap<String, String> for branchName : commitID.
11. `private static String headID` Head Commit hash ID.
12. `private static String activeBranchName`  Active branch name in String.
13. `private static HashMap<String, String> addFileMap` HashMap<String, String> of fileName : fileHash for all files staged to add.
14. `private static HashMap<String, String> rmFileMap` HashMap<String, String> of fileName : fileHash for all files staged to remove.
15. `private static Commit headCommit` The head Commit object. This variable is not stored as file.


### Class 3: Commit
This class represents a `Commit` object that will be stored as a file. The file name is a unique SHA1 hash of the file content.
#### Fields
1. `public String message` The message the user provides during making the Commit.
2. `public Date timeStamp` The Date object representing the timestamp when the Commit is cretaed.
3. `private ArrayList<String> parentIDs` The parent commit IDs for the commit, stored as String in an ArrayList.
4. `public Map<String, String> blobs` The HashMap storing all the committed files in the format of fileName: fileHash.


## Algorithms

### Repository Class
The main logics reside in the **Repository class**.

1. `public static void init()` 
   1. Used for `java gitlet.Main init` command. 
   2. Create the necessary directories and make the initial commit.
   3. If there is already a Gitlet version-control system in the current directory, it will exit. 
2. `public static void checkInitialization()` 
   1. Check if .getlet directory is initialized. It should be called before any gitlet operations in Main except the init().
   2. Print a warning if CWD doesn't have `gitlet`system initialized.
3. `public static String saveCommitToSHA1Name(File destFolder, Commit c)`
   1. Save the Commit object to a file in the destination folder.
   2. Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
   3. Return the SHA1 hash.
4. `public static String copyFileToSHA1Name(File destFolder, File f)`
   1. Copy the file to the destination folder.
   2. Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
   3. Return the SHA1 hash.
5. `public static String copyFileToSHA1Name(File destFolder, File f)`
   1. Copy the file to the destination folder.
   2. Get the SHA1 hash of the file, and change the file name to the SHA1 hash.
   3. Return the SHA1 hash.
6. `public static void readStaticVariables()`
   1. Read the static variables from their corresponding files.
   2. This is used before any function that requires these variables.
7. `public static void saveStaticVariableFiles()`
   1. Write the static variable file contents to the corresponding files.
8. `public static Commit readCommitFromFile(String commitID)`
   1. Read from the file in COMMITS_DIR to a Commit object.
   2. The file name is commitID in String.
9. `public static void printVariables()`
   1. Helper function for debugging.
   2. This prints the static variables' contents.
10. `public static void add(String fileName)`
    1. Used for `java gitlet.Main add [file name]` command.
    2. Conduct file operation here and provide information to the `addFileMap` so later the makeCommit can update the commit's fileBlos information. 
    3. Copy the file to `blobs` folder and update `addFileMap` as below:
       1. If headCommit blobs have this fileName : hash pair. Then no need to stage this file. 
          1. If this file is already staged, remove it from addFileMap.
       2. Else, add fileName : hash to addFileMap. This will create or update the fileHash in the map.
    4. Check if the file exists in the rmFileMap, if so remove it there.
11. `public static void rmFile(String fileName)`
    1. Used for `java gitlet.Main rm [file name]` command.
    2. Remove file if needed and stage a file to be removed for the next commit. Finish the file operation here and update information in the `addFileMap` so the next commit can update the commit fileBlob.
    3. If the file name is in the `addFiles`, remove the file in the addFileMap list and remove the corresponding file.
    4. If the file is in the current commit's blobs, 
       1. Add the fileName : hash to the `rmFileMap`. 
       2. If the file exists in CWD, delete it.
    5. If neither is the case, exit and print error message.
    6. Note that a file will not be both in the addFiles and current commit's blobs. See the add and makeCommit functions.
        1. After each commit, addFiles will be cleared.
        2. If the file is in the current commit's blobs, and we add this file again, it will not be staged in the addFiles.
12. `public static void makeCommit(String message, additionalParentID)`
    1. Used for `java gitlet.Main commit [message]` command and `merge` function.
    2. Create a Commit object with the provided message. Save the Commit object to `commits` folder.
    3. For `merge` function, provide the `additionalParentID` to be added to the merged commit.
    4. Use the static instance variable `headCommit` create a new Commit object by the copying constructor.
    5. Update the Commit object instance variables based on the addFileMap and rmFileMap:
       1. No file operation here. Just update the record (pointers).
       2. Update message.
       3. Update fileBlobs.
          1. For each `file:hash` in the `addFiles`, update it in the `blobs` section of the commit. The file copy operation is already done in the `add` function.
          2. For each `file:hash` in the `rmFiles`, remove it from the `blobs` section of the commit. The file deletion operation (if needed) is already done in the `rm` function call.
       4. Save the commit object.
       5. Update headID and branch pointer.
    6. Clear staging area.
    7. Save static variables.
13. `public String showLocalLog()`
    1. Used for `java gitlet.Main log` command.
    2. Print the log history of the headCommit.
    3. Create a new StringBuilder object.
    4. Start from the headCommit, read the commitID, data and message, and its parent commitID.
    5. From the parent commitID, read its contents and add them to the StringBuilder. If there are multiple parentCommitIDs, use the first one.
    6. Repeat until reaching the initial commit where the parent commitID is null.
    7. Display the whole string content.
14. `public String showGlobalLog()`
     1. Used for `java gitlet.Main global-log` command.
     2. Show all commmits in no particular order.
     3. Iterate all the file names in `commits` folder.
     4. Read the files to Commit object, then its contents into a string joiner object.
15. `public static void showGlobalLogInOrder()`
    1. Show all commits in the order of timestamp.
    2. Implement the Commit class as Comparable and change the compareTo() based on timestamp.
    3. Use a TreeMap <Commmit, String> to store Commit:ID information in reverse order.
    4. Iterate the TreeMap, it will return objects in reversed order from latest timestamp to oldest timestamp.
16. `public String findCommitFromMessage(String message)`
    1. Used for `java gitlet.Main find [commit message]` command.
    2. Find all commit IDs having the provided message.
    3. Iterate all the file names in commit folder. Save commit ID where the Commit has the provided message.
17. `public String showStatus()`
    1. Used for `java gitlet.Main status` command.
    2. Print the repository information such as branches, staged files etc.
    3. From `branchesMap`, put all branch names in a TreeSet so they will be in lexicographic order.
    4. From `addFileMap` and `rmFileMap` read the list of file names to TreeSet.
    5. Add modified files but not staged for commit.
    6. Add untracked file names which is neither in `addFileMap` nor in the headCommit fileBlobs.
18. `public static TreeMap<String, String> getModifiedButNotTrackedFiles()`
    1. Helper function for showStatus() to get the modified but not tracked files.
    2. Use a TreeMap to keep ordering and a String maker to differentiate between modified and deleted.
    3. Find files that were committed before which now are changed but not staged in addFileMap.
    4. Find files that were staged before which now are changed after being staged.
    5. Find files that were deleted from CWD, still tracked in current commit but they are not in rmFileMap.
19. `public void checkoutCommitSpecificFile(String commitID, String fileName)`
    1. Used for `java gitlet.Main checkout [commit id] -- [file name]` command.
    2. Change the file's contents according to its snapshot in the provided commitID.
    3. Use the commitID to read the relevant commit object.
    4. From the commit object, get the file's hash.
    5. Use the hash, get the serialized file from the blobs folder, copy it to the CWD and overwrite the file if it is already there.
20. `public void checkoutFile(String fileName)`
    1. Used for `java gitlet.Main checkout -- [file name]` command.
    2. Change a file's content according to the headCommit.
    3. Call checkoutCommitSpecificFile(headID, fileName)
21. `public void checkoutBranch(String branchName)`
    1. Used for `java gitlet.Main checkout [branch name]` command.
    2. Change the CWD contents according to the branch's latest commit.
    3. Use the branch name to get the branch latest commitID from `branchesMap`.
    4. Call `resetCommitFiles` to copy files to CWD.
    5. Point the `head` file to this commit. Change the activeBranch to the branchName.
    6. Clear the stagingArea.
22. `public void createBranch(String branchName)`
    1. Used for `java gitlet.Main branch [branch name]` command.
    2. Create a new branch and point it to the headCommit.
    3. Update the branchesMap, add the branchName : headID
23. `public void rmBranch(String branchName)`
    1. Used for `java gitlet.Main rm-branch [branch name]` command.
    2. Remove the provided branch from the repository.
    3. Update the `branches` file, delete the branchName entry.
24. `public void resetCommit(String preCommitID)`
    1. Used for `java gitlet.Main reset [commit id]` command.
    2. Change CWD contents according to the provided commit id. Update head pointer and branch pointer.
    3. Call helper function resetCommitFiles().
    4. Change the head pointer and branch pointer to preCommitID.
    5. Clear the stagingArea.
25. `public void resetCommitFiles(String preCommitID)`
    1. Helper function to change CWD contents based on the provided commit id.
    2. Read the commit object with the preCommitID.
    3. Check if the commit file exists and if there is any untracked files in CWD. Call helper function `handleUntrackedFileOverwritten`.
    4. Delete files in CWD that are already committed in the head commit. Note not all files will be deleted. For example, an untracked new file which is not in the commit, and if it will not be overwriten by the preCommit, it should not be deleted.
    5. Copy files from the preCommit blobs to CWD, and overwrite if the fileName already exists. The final status is the all the fileNames in preCommitID are restored. Some new files might exist.
26. `public static void handleUntrackedFileOverwritten(String curCommitID, String preCommitID)`
    1. Helper function for resetCommitFiles.
    2. Handle the case where there is untracked file which will be overwritten by the preCommitID.
27. `public void merge(String givenBranchName)`
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
                2. if they are modified in the current branch, we have a conflict.
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
       3. Handle case 4.2.1: the given branch deleted files
          1. Set operation to filter the files in the common ancestor but not in the given branch
          2. Set operation to filter the files that are present in the current branch
             1. If the file has the same hash in current branch as the hash in the ancestor, they should be staged for removal. 
             2. If not, call handleConflict function.
       4. Handle case 4.2.2: the given branch added new files
          1. find the files in the given branch but not in the ancestor
          2. filter the files that are not present in the current branch, they should be staged for add
          3. filter the files that are present in the current branch, and if they have different hash code between given branch and current branch, call handleConflict helper function.
       5. Handle case 4.2.3: the given branch modified files from ancestor
          1. find files that are both in given branch and ancestor but they have different hash 
          2. filter the files that are absent in the current branch, add them (to stagingArea? or copy to CWD?)
          3. filter the files that are present in the current branch
              1. if these files in current branch have different hash in the given branch
                 1. If in the current branch, those files are the same as the ancestor, stage them for add.
                 2. If in the current branch, those files are the different from the ancestor, and different from the current branch, call handleConflict helper function.
       6. At the end, make a new commit
28. `public static void givenBranchDeletesFiles( String ancestorID, String givenBranchID, String activeBranchID)`
    1. Helper function for merge. Handle case where the givenBranch deletes file from the ancestor.
    2. 
29. `public Commit findLatestCommonAncestor(String branch1, String branch2)`
    1. Helper function for merge
    2. Find the latest common ancestor for two branches
       1. use two pointers, switch position when reaching the end.
       2. when they are equal, we found the ancestor.
30. `public void handleConflict(String file1, String file2) `
    1. Helper function to handle merge conflicts
31. `public Set<String> toSet(Commit c)` 
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
    ├── stagingADD              <==== A serialized hashmap<String, String> stores fileName : hashID for files staged to add.
    ├── stagingRM               <==== A serialized hashmap<String, String> stores fileName : hashID for files staged to remove.
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