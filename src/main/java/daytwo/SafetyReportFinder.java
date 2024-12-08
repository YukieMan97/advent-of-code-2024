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

                String currResultDiff = findSafeDiff(prevLvl, currLvl);
                boolean isInc;

                switch (currResultDiff) {
                    case INCREASING -> isInc = true;
                    case DECREASING -> isInc = false;
                    default -> {
                        handleUnsafeReports(rowOfLevels, index, line, currResultDiff);

                        continue;
                    }
                }

                String prevResultDiff;
                String consistentResultDiff = isConsistentDiff(isInc, currResultDiff);
                index = 2;

                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevResultDiff = currResultDiff;
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);

                    currResultDiff = findSafeDiff(prevLvl, currLvl);
                    consistentResultDiff = isConsistentDiff(isInc, currResultDiff);

                    if (consistentResultDiff == UNSAFE) {
                        handleUnsafeReports(rowOfLevels, index, line, prevResultDiff);

                        break;
                    }

                    index++;
                }

                if (consistentResultDiff != UNSAFE) {
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

    protected static String isConsistentDiff(boolean isInc, String diffRes) {
        if (isInc && diffRes == INCREASING) {
            return INCREASING;
        }

        if (!isInc && diffRes == DECREASING) {
            return DECREASING;
        }

        return UNSAFE;
    }

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
            int currUnsafeIndex,
            String line,            // for sout
            String resDiff
    ) {
        System.out.println(line);
        this.unsafeReports.add(rowOfLevels);
    }
}
