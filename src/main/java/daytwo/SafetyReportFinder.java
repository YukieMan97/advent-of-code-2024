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

    protected final static String POSITIVE = "POSITIVE";
    protected final static String NEGATIVE = "NEGATIVE";
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

                List<Integer> rowOfLevels = Arrays.stream(line.split("\\s"))
                        .map(Integer::parseInt)
                        .toList();

                // assumption: there are at least 5 levels
                int index = 0;
                prevLvl = rowOfLevels.getFirst();
                currLvl = rowOfLevels.get(index + 1);

                String resDiff = findSafeDiff(prevLvl, currLvl);
                boolean isInc;

                switch (resDiff) {
                    case POSITIVE -> isInc = true;
                    case NEGATIVE -> isInc = false;
                    default -> {
                        handleUnsafeReports(rowOfLevels, index, line, resDiff);

                        continue;
                    }
                }

                String res = isIncSafeDiff(isInc, resDiff);
                index = 2;

                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);

                    resDiff = findSafeDiff(prevLvl, currLvl);
                    res = isIncSafeDiff(isInc, resDiff);

                    if (res == UNSAFE) {
                        handleUnsafeReports(rowOfLevels, index, line, resDiff);

                        break;
                    }

                    index++;
                }

                if (res != UNSAFE) {
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

    protected static String isIncSafeDiff(boolean isInc, String diffRes) {
        if (isInc && diffRes == POSITIVE) {
            return POSITIVE;
        }

        if (!isInc && diffRes == NEGATIVE) {
            return NEGATIVE;
        }

        return UNSAFE;
    }

    protected static String findSafeDiff(int prevNum, int currNum) {
        int diff = currNum - prevNum;

        if (diff == 0) {
            return EQUAL;
        }

        if (diff > 0 && diff <= 3) {
            return POSITIVE;
        }

        if (diff < 0 && diff >= -3) {
            return NEGATIVE;
        }

        return BIG_DIFF;
    }

    private void handleUnsafeReports(
            List<Integer> rowOfLevels,
            int currUnsafeIndex,
            String line,
            String resDiff
    ) {
        System.out.println(line);

        // keep track of unsafe indexes?

        // cases to account for
        int posCnt = 0;
        int negCnt = 0;
        int eqCnt = 0;

        // positive > negative and/or equal, remove at most 1 level
            // safe:   10 20 30 40 60 50  or  10 20 30 40 50 50
            // unsafe: 10 20 30 40 30 40  or  10 20 30 40 30 20
        // negative > positive and/or equal, remove at most 1 level
            // safe:   60 50 40 50 30  or  60 50 40 30 20 20
            // unsafe: 60 50 40 50 40  or  60 50 40 50 60
        // unsafe when negative == positive, positive == equal, or negative == equal

        this.unsafeReports.add(rowOfLevels);
    }
}
