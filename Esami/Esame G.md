
**Question #1:** Describe the different approaches used in literature to evaluate the performance of a search system. The answer should cover at least the definitions of _F-measure_, _MAP_, and _MRR_. Why these metrics belong to different lines of literature? What are the main differences between them?

**Question #2:** Build a toy example (5 documents, 1 query) that you can use to introduce and discuss the concepts of _binary term-document incidence matrix_, _term-document count matrix_ and _term-document frequency matrix_. What is the matrix used to define TF-IDF? What are the main characteristics of TF-IDF?

**Question #3:** Simulate the evaluation of the query `Pippo AND NOT Pluto` using the **Term-At-A-Time (TAAT)** strategy, given the following inverted lists:

- Pippo: 1, 2, 4, 8
    
- Pluto: 2, 3, 4, 10, 11
    

Show the main steps of the query processing and determine the set of documents that satisfy the query.

**Question #4:** Encode the number 9 using both **Elias Gamma** and **Elias Delta** coding schemes.

- Provide the encoded results for both methods.
    
- Explain why an integer code must be prefix-free.
    
- Describe the main advantages and disadvantages of variable byte with respect to Elias Gamma and Delta.