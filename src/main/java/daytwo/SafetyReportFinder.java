package daytwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Day2 Part1
 */
public class SafetyReportFinder {
    private int numSafeReports = 0;
    private final List<List<Integer>> safeReports;
    private final List<List<Integer>> unsafeReports;

    protected final static String INCREASING = "POSITIVE";
    protected final static String DECREASING = "NEGATIVE";
    protected final static String EQUAL = "EQUAL";
    protected final static String BIG_DIFF = "BIG_DIFF";
    protected final static String UNSAFE = "UNSAFE";

    public SafetyReportFinder() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");

        assert inputStream != null;
        InputStreamReader inStreamReader = new InputStreamReader(inputStream);

        this.safeReports = new ArrayList<>();
        this.unsafeReports = new ArrayList<>();
        this.numSafeReports = findSafeAndUnsafeReports(inStreamReader);
    }

    public int getNumSafeReports() {
        return numSafeReports;
    }

    public List<List<Integer>> getUnsafeReports() {
        return unsafeReports;
    }

    public List<List<Integer>> getSafeReports() {
        return safeReports;
    }

    private int findSafeAndUnsafeReports(InputStreamReader inStreamReader) throws IOException {
        int prevLvl;
        int currLvl;
        int numSafeReports = 0;
        int numEntries = 0;

        try (BufferedReader reader = new BufferedReader(inStreamReader)) {
            while (reader.ready()) {
                numEntries++;
                String line = reader.readLine();
//                System.out.println(line);

                // if first and last indexes are unsafe, then they should be considered safe
                // if middle indexes are unsafe...

                List<Integer> rowOfLevels = Arrays.stream(line.split("\\s"))
                        .map(Integer::parseInt)
                        .toList();

                // assumption: there are at least 5 levels
                int index = 0;
                prevLvl = rowOfLevels.getFirst();
                currLvl = rowOfLevels.get(index + 1);

                String currDiff = findSafeDiff(prevLvl, currLvl);
                boolean isInc;

                switch (currDiff) {
                    case INCREASING -> isInc = true;
                    case DECREASING -> isInc = false;
                    default -> {
                        handleUnsafeReports(
                            rowOfLevels,
                            index + 1,
                            line,
                            null,
                            currDiff
                        );

                        continue;
                    }
                }

                String prevDiff;
                String validatedDiff = validateDirectionConsistency(isInc, currDiff);
                index = 2;

                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevDiff = currDiff;
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);

                    currDiff = findSafeDiff(prevLvl, currLvl);
                    validatedDiff = validateDirectionConsistency(isInc, currDiff);

                    if (validatedDiff == UNSAFE) {
                        handleUnsafeReports(rowOfLevels, index, line, prevDiff, currDiff);

                        break;
                    }

                    index++;
                }

                if (validatedDiff != UNSAFE) {
                    this.safeReports.add(rowOfLevels);

                    numSafeReports++;
                }
            }
        }

        System.out.println("numEntries: " + numEntries + "; expected 1000");
        System.out.println("numSafeReports: " + numSafeReports + "; expected 516");
        System.out.println("safeReports.size: " + safeReports.size() + "; expected 516");
        System.out.println("unsafeReports.size: " + unsafeReports.size() + "; expected 484");

        return numSafeReports;
    }

    protected static String validateDirectionConsistency(boolean isInc, String resultDiff) {
        if (isInc && resultDiff == INCREASING) {
            return INCREASING;
        }

        if (!isInc && resultDiff == DECREASING) {
            return DECREASING;
        }

        return UNSAFE;
    }

    /**
     * Safe Difference means increasing or decreasing and a difference of 1-3 (inclusive).
     * @param prevNum
     * @param currNum
     * @return
     */
    protected static String findSafeDiff(int prevNum, int currNum) {
        int diff = currNum - prevNum;

        if (diff == 0) {
            return EQUAL;
        }

        if (diff > 0 && diff <= 3) {
            return INCREASING;
        }

        if (diff < 0 && diff >= -3) {
            return DECREASING;
        }

        return BIG_DIFF;
    }

    private void handleUnsafeReports(
            List<Integer> rowOfLevels,
            int indexToCheck,
            String line,            // for sout
            String prevDiff,
            String currDiff
    ) {
        System.out.println(line);
        this.unsafeReports.add(rowOfLevels);


    }
}
