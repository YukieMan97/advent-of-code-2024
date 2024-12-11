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
    private final List<List<Integer>> safeReports;
    private final List<List<Integer>> unsafeReports;

    protected final static String INCREASING = "POSITIVE";
    protected final static String DECREASING = "NEGATIVE";
    protected final static String EQUAL = "EQUAL";
    protected final static String BIG_DIFF = "BIG_DIFF";
    protected final static String UNSAFE = "UNSAFE";
    protected final static String SAFE = "SAFE";

    public SafetyReportFinder() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input_sample.txt");

        assert inputStream != null;
        InputStreamReader inStreamReader = new InputStreamReader(inputStream);

        this.safeReports = new ArrayList<>();
        this.unsafeReports = new ArrayList<>();

        findSafeAndUnsafeReports(inStreamReader);
    }

    public int getNumSafeReports() {
        return this.unsafeReports.size();
    }

    public List<List<Integer>> getUnsafeReports() {
        return unsafeReports;
    }

    public List<List<Integer>> getSafeReports() {
        return safeReports;
    }

    private void findSafeAndUnsafeReports(InputStreamReader inStreamReader) throws IOException {
        int prevLvl;
        int currLvl;
        int numEntries = 0;

        try (BufferedReader reader = new BufferedReader(inStreamReader)) {
            while (reader.ready()) {
                numEntries++;
                String line = reader.readLine();
                System.out.println(line);

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
                        this.unsafeReports.add(rowOfLevels);

                        continue;
                    }
                }

                String validatedDiff = validateDirectionConsistency(isInc, currDiff);
                index += 2;

                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);

                    currDiff = findSafeDiff(prevLvl, currLvl);
                    validatedDiff = validateDirectionConsistency(isInc, currDiff);

                    if (validatedDiff == UNSAFE) {
                        this.unsafeReports.add(rowOfLevels);

                        break;
                    }

                    index++;
                }

                if (validatedDiff != UNSAFE) {
                    this.safeReports.add(rowOfLevels);
                }
            }
        }

        int totalNumEntries = 1000;
        int nonTolerableSafeReports = 516;
        System.out.println("numEntries: " + numEntries + "; expected " + totalNumEntries);
//        System.out.println("Non-Tolerable safeReports.size: " + safeReports.size() + "; expected " + nonTolerableSafeReports);
//        System.out.println("Non-Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (totalNumEntries - nonTolerableSafeReports));

        System.out.println("Tolerable safeReports.size: " + safeReports.size() + "; expected ?");
        System.out.println("Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (totalNumEntries - safeReports.size()));
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
}
