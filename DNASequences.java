import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Recombinate a long DNA sequence from multiple overlapping sequences.
 */
public class DNASequences {

    public static void main(String[] args) {

        // Valid input is either 1 or 2
        Path inputPath;
        Path solutionPath = null;
        if (args.length >= 1 && args.length <= 2) {
            inputPath = Paths.get(args[0]);

            if (args.length == 2) {
                solutionPath = Paths.get(args[1]);
            }

        } else {
            System.out.println("Usage: DNASequences fragments_input_file [solution_file]");
            return;
        }

        List<String> orderedSequences = new LinkedList<>();
        List<String> sequences;
        try {
            sequences = parseInput(inputPath);
        } catch (Exception e) {
            System.out.println("Error parsing Input File");
            e.printStackTrace();
            return;
        }

        // Do all of the stuff
        List<String> sortedSequences = sortSequences(sequences);
        String sortedAndCollapsedSequences = collapseSortedSequence(sortedSequences);

        // If this is a test, with solution provided, then check the derived path against the solution
        // and output appropriate information about success/failure
        if (solutionPath != null) {
            String solution;
            try {
                solution = parseSolution(solutionPath);
            } catch (Exception e) {
                System.out.println("Error parsing Solution File");
                e.printStackTrace();
                return;
            }
            System.out.println(sortedAndCollapsedSequences.equals(solution.toString()) ? "Solutions match!" : "Solutions do NOT match.");

        // If it is NOT a test, no solution provided, then output the sequenced data in rows of 80 characters each
        } else {
            for (int i = 0; i < sortedAndCollapsedSequences.length(); i++) {
                System.out.print(sortedAndCollapsedSequences.charAt(i));
                if ((i + 1) % 80 == 0) {
                    System.out.println();
                }
            }
        }
    }

    /**
     * Given a list of unordered, partial sequences, determine the order by looking for overlapping substrings.
     *
     * @param sequences
     * @return A string of ordered sequences, but collapsed such that there is no overlap,
     * just a continuous unbroken sequence
     */
    private static List<String> sortSequences(List<String> sequences) {

        List<String> sortedSequences = new LinkedList<>();
        boolean stagnant;

        // Prime the sortedSequences with a sequence. Makes no difference which. The first will suffice.
        sortedSequences.add(sequences.get(0));
        sequences.remove(0);

        // Loop until a complete pass through the sequences yields no matches; a completely stagnant cycle. This
        // is better than looping until there are no more sequences (cuz they were all moved to the sorted list). In
        // the case of duplicate sequences, there will be some ignorable leftovers.
        do {
            stagnant = true;

            // Loop through all of the sequences looking for things to add to the start or end of the sortedSequences
            for (ListIterator<String> iter = sequences.listIterator(); iter.hasNext(); ) {
                String sequence = iter.next();

                // Check to see if the sequence fits at the beginning of the sorted list.
                {
                    String firstSortedSequence = sortedSequences.get(0);

                    // The midpoint is the halfway + 1 of the smaller of the two strings.
                    int midpoint = ((sequence.length() <= firstSortedSequence.length() ?
                            sequence.length() : firstSortedSequence.length()) / 2) + 1;
                    String back = sequence.substring(sequence.length() - midpoint);
                    int index = sortedSequences.get(0).indexOf(back);
                    if (index >= 0) {
                        String overlap = firstSortedSequence.substring(0, index + back.length());
                        if (sequence.contains(overlap)) {
                            // Checked both ways, it's a match! There's some tidying up to do now.
                            //   1) Add the sequence to the beginning of the sorted list.
                            sortedSequences.add(0, sequence);
                            //   2) Remove it from the list of sequences being tested. It found its home.
                            iter.remove();
                            //   3) This was not a stagnant pass - something happened!
                            stagnant = false;
                            //   4) Jump back to the top of the loop, nothing left to check for this sequence.
                            continue;
                        }
                    }
                }

                // Check to see if the sequence fits at the end of the sorted list.
                {
                    String lastSortedSequence = sortedSequences.get(sortedSequences.size() - 1);

                    // The midpoint is the halfway + 1 of the smaller of the two strings.
                    int midpoint = ((sequence.length() <= lastSortedSequence.length() ?
                            sequence.length() : lastSortedSequence.length()) / 2) + 1;
                    String front = sequence.substring(0, midpoint);
                    int index = lastSortedSequence.indexOf(front);
                    if (index >= 0) {
                        String overlap = lastSortedSequence.substring(index);
                        if (sequence.contains(overlap)) {
                            // Checked both ways, it's a match! There's some tidying up to do now.
                            //   1) Add the sequence to the end of the sorted list.
                            sortedSequences.add(sequence);
                            //   2) Remove it from the list of sequences being tested. It found its home.
                            iter.remove();
                            //   3) This was not a stagnant pass - something happened!
                            stagnant = false;
                            //   4) No continue needed - already at the end of this loop
                        }
                    }
                }
            }
        } while (!stagnant);

        return sortedSequences;
    }

    /**
     * Given an ordered set of sequences with overlap from one to the next, collapse them down to a single
     * un-overlapped sequence.
     *
     * This is a kind of cludgy way to do it. It would have been nice to use the "overlap" info in the sorting
     * method, but unless you are storing overlaps per sequence it didn't work. It turns out that this is a cleaner
     * implementation even though it requires another pass through the data.
     *
     * Basic idea is that you take two adjacent sequences. See if the end of the first matches the beginning of
     * the second by comparing smaller and smaller strings until a match is found.
     *
     * @param sortedSequences
     * @return
     */
    private static String collapseSortedSequence(List<String> sortedSequences) {
        String collapsedSortedSequence = sortedSequences.get(0);

        for (int i = 0; i < sortedSequences.size() - 1; i++) {
            String first = sortedSequences.get(i);
            String second = sortedSequences.get(i+1);

            for (int j = (first.length() <= second.length() ? first.length() : second.length()); j > 0; j--) {
                if (second.substring(0, j).equals(first.substring(first.length() - j))) {
                    collapsedSortedSequence += second.substring(j);
                    break;
                }
            }
        }
        return collapsedSortedSequence;
    }


    /**
     * Input file will have the form of individual sequences containing line breaks, and being separated by a line
     * with the form ">characters_numbers"
     *
     * 2-sequence example:
     *
     * >Rosalind_1836
     * GCGCCCGGGGCAAGAGTCATTATACTTGAGAATATACATTTAACAGCGGGCTCATAGCAC
     * AGCAGTTATAAAAGAGGCAGATTCCGACCCCTTAGGGACTATAGGTTTTCTGG
     * >Rosalind_5142
     * CGATACGCTAGCCTGGCATTCCCAAATAGGCGTTGCGTACGCATGCCTAAGCGCCGGGAG
     * AGAATTGGAACAGTCTACCTAACCCGGCTTATAAC
     *
     * The goal is to remove separators and create a list of Strings, where each list entry is
     * a different sequence.
     *
     * @param inputPath
     * @return
     * @throws IOException
     */
    static List<String> parseInput(Path inputPath) throws IOException {
        List<String> lines = Files.readAllLines(inputPath);
        List<String> sequences = new ArrayList<>();

        String currentSequence = new String();
        boolean firstLine = true;
        for (String currentLine : lines) {
            if (currentLine.startsWith(">")) {
                if (!firstLine) {
                    sequences.add(currentSequence);
                }
                currentSequence = "";
            } else {
                currentSequence += currentLine;
            }
            firstLine = false;
        }
        sequences.add(currentSequence);
        return sequences;
    }

    /**
     * Input file will have the form of individual sequences containing line breaks,
     * representing a long, unbroken string
     *
     * Example:
     *
     * GCGCCCGGGGCAAGAGTCATTATACTTGAGAATATACATTTAACAGCGGGCTCATAGCAC
     * AGCAGTTATAAAAGAGGCAGATTCCGACCCCTTAGGGACTATAGGTTTTCTGG
     * CGATACGCTAGCCTGGCATTCCCAAATAGGCGTTGCGTACGCATGCCTAAGCGCCGGGAG
     * AGAATTGGAACAGTCTACCTAACCCGGCTTATAAC
     *
     * The goal is to remove line breaks.
     *
     * @param inputPath
     * @return
     * @throws IOException
     */
    static String parseSolution(Path inputPath) throws IOException {
        List<String> lines = Files.readAllLines(inputPath);
        String sequences = "";

        for (String currentLine : lines) {
            sequences += currentLine;
        }
        return sequences;
    }
}
