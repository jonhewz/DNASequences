*Challenge Introduction:*
To sequence the DNA for a given individual we typically fragment each chromosome to many small pieces that can be sequenced in parallel and then re-assemble the sequenced fragments into one long DNA sequence. In this task we ask that you take on a specific subtask of this process.

*Challenge:*
The input to the problem is at most 50 DNA sequences (i.e, the character set is limited to T/C/G/A) whose length does not exceed 1000 characters. The sequences are given in FASTA format https://en.wikipedia.org/wiki/FASTA_format. These sequences are all different fragments of one chromosome.

The specific set of sequences you will get satisfy a very unique property: there exists a unique way to reconstruct the entire chromosome from these reads by gluing together pairs of reads that overlap by more than half their length.

The output of the program should be this unique sequence that contains each of the given input strings as a substring.

*Example data set [small]:*
>Frag_56
ATTAGACCTG
>Frag_57
CCTGCCGGAA
>Frag_58
AGACCTGCCG
>Frag_59
GCCGGAATAC
Expected output of small data set:
ATTAGACCTGCCGGAATAC

*Solution:*
Compile using the following command
~/dev/DNASequences$ javac DNASequences.java

Run in production mode by specifying an input set of sequences. The result will be output to the terminal.
~/dev/DNASequences$ java DNASequences full_data_set.txt

Run in test mode by specifying both an input set of sequences as well as the solution. Output will say whether the program's solution matches teh provided solution.
~/dev/DNASequences$ java DNASequences full_data_set.txt full_solution.txt
