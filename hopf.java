import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author nagendralimbu
 */
public class hopf {

    static ArrayList<int[]> sPatterns;
    static ArrayList<int[]> cPatterns;
    private static double stable;

    int[][] weights;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            hopf hop = new hopf(new File(args[0]), new File(args[1]));
            // proceed only if the network is stable 
            if (stable < 0.138) {
                hop.trainPatterns();
                hop.recall(cPatterns);
            }
        } catch (IOException e) {

        }
    }

    /**
     *
     * @param pattern1
     * @param pattern2
     * @throws IOException
     */
    public hopf(File pattern1, File pattern2)
            throws IOException {

        sPatterns = new ArrayList<>();
        cPatterns = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(pattern1));
            String line = br.readLine();
            String[] s = line.split(" ");
            weights = new int[s.length][s.length];
        } catch (IOException ex) {
        }

        formulatePatterns(sPatterns, pattern1);
        formulatePatterns(cPatterns, pattern2);

        double tPat = sPatterns.size();
        double tNns = sPatterns.get(0).length;
        stable = tPat / tNns;
    }

    /**
     * Extract the patterns from file into an array
     *
     * @param arrayList
     * @param pattern
     * @throws IOException
     */
    private static void formulatePatterns(ArrayList<int[]> arrayList, File pattern)
            throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(pattern));
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] sArray = sCurrentLine.split(" ");
            int[] patterns = new int[sArray.length];
            for (int i = 0; i < sArray.length; i++) {
                patterns[i] = Integer.parseInt(sArray[i]);
            }
            arrayList.add(patterns);
        }
    }

    /**
     * This method computes the cumulative weight matrix to be used to recall a
     * corrupted pattern.
     */
    public void trainPatterns() {
        sPatterns.stream().forEach((pattern) -> {
            int length = pattern.length;
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    weights[i][j] = weights[i][j] + (pattern[i] * pattern[j]);
                }
            }
        });
    }

    /**
     * This method checks against the stored weight matrix to see if a corrupted
     * pattern can be recalled.
     *
     * @param patterns
     */
    public void recall(ArrayList<int[]> patterns) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int[] pattern : patterns) {
                int length = pattern.length;
                for (int i = 0; i < length; i++) {
                    int value = 0;
                    for (int j = 0; j < length; j++) {
                        if (j != i) {
                            value += pattern[j] * weights[i][j];
                        }
                    }
                    if (pattern[i] != getUpdateInput(value)) {
                        changed = true;
                        pattern[i] = getUpdateInput(value);
                    }
                }
            }

        }
        patterns.stream().map((pattern) -> {
            for (int i : pattern) {
                System.out.print(i + " ");
            }
            return pattern;
        }).forEach((_item) -> {
            System.out.println("\n");
        });
    }

    /**
     *
     * @param value
     * @return
     */
    private int getUpdateInput(int value) {
        if (value >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

}
