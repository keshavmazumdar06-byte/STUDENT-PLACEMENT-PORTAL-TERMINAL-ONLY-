# DSA notes and viva

## BST
CGPA is the key. Equal CGPA values share a bucket. Range search prunes subtrees outside the requested interval. Average operations are O(log n) in a balanced tree; an unbalanced BST can degrade to O(n).

## Hashing
Student ID is hashed to an array index. Separate chaining handles collisions. Average lookup is O(1); worst case O(n). The table resizes when load factor exceeds 0.75.

## Priority Queue
A custom Max Heap keeps the highest priority candidate at index 0. Insert and extract are O(log n); peek is O(1). Priority is CGPA, then number of listed skills, then deterministic student ID tie-break.

## Merge Sort
Divide-and-conquer stable sorting by descending CGPA. Time O(n log n), auxiliary space O(n).

## Viva questions
1. What is a BST and how does range search work?
2. Why can an unbalanced BST become O(n)?
3. What is a hash collision?
4. Why use separate chaining?
5. What is load factor?
6. How does a Max Heap maintain its property?
7. Why are heap insert and extract O(log n)?
8. Explain Merge Sort and its complexity.
9. Why does this project use file serialization?
10. How are duplicate job applications prevented?
