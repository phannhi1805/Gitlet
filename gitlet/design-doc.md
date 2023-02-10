# GITLET DESIGN DOCUMENT
**Name:** Nhi Phan

##Classes and Data Structure

###Commit
* message - commit message
* timestamp - Time at which the commit was made
* id - SHA-1 code of each commit
* blobs - a hashmap which contains all the files (blobs) each commit has to keep track of. K is file id, and V is blob
* parent - the id of previous commit which this.commit copy over initially





###Repository



###Blob
* 



##Algorithms

###Commit
* get methods: All the get methods to access Commit instant variables
* makeTimestamp: method to convert current time to desired format
* init: a section of init that handles all the commit related info
* saveCommit: Save a commit to a file in the Commit Folder 

###Repository
* init: the second section of init which initializes all the directories
* add: Look into staging area 
  * if same name is there, remove it 
    * if not in last commit, put fileName in
  * if not, put file name in
    
* commmit: Get head commit, get its ID and blobs
    * fix the blobs according to Staging area
    * create commit and save it
    * Clean out Staging Area
  
* checkout number 1 and 2: 
  * if it is head checkout
    * Get head commit id
  * Use the id to get its blobs 
  * Check if file is in blobs 
    * if not, error, stop
  * Check if file name is in CWD 
    * if yes, delete it
  * Put file in CWD

###Blob

##Persistence
<div class="mermaid">
     graph TD
      CWD --- .gitlet
      .gitlet --- Staging-Area
.gitlet --- Commit-Folder
.gitlet --- HEAD
Staging-Area --- Staging-for-addition
Staging-Area --- Staging-for-removal
</div>

