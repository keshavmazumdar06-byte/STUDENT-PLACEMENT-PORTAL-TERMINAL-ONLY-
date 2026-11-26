# Placement Portal — Pure Core Java

Terminal-only academic project. Java 17+, standard library only. No GUI, web stack, database server, Maven, or external dependencies.

## Run on Windows PowerShell
From the project root:
```powershell
javac -d out src/Main.java
java -cp out Main
```
On macOS/Linux, the same commands work after creating the output directory if desired.

## Features
- Student add/list/search/update/delete
- Company and job creation/listing
- Eligibility by CGPA, branch, backlog count and required skills
- Apply for jobs with duplicate-application prevention
- Merit list using custom Merge Sort
- Candidate shortlisting using custom Max Heap
- DSA lab for BST, hashing, heap and sorting
- Serialized file persistence at `data/portal.dat`
- Sample data on first run

## DSA
- BST: CGPA index with duplicate CGPAs stored in one node; range search.
- Hashing: custom array hash table, separate chaining and resizing, keyed by student ID.
- Priority Queue: custom Max Heap ordered by CGPA, skill count, then deterministic ID tie-break.
- Sorting: stable Merge Sort by descending CGPA.

Note: this is a local, single-user academic console program, not a production multi-user placement system.
