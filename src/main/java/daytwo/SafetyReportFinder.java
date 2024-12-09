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
    protected final static String SAFE = "SAFE";

    public SafetyReportFinder() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input_sample.txt");

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
                            null,
                            currDiff
                        );

                        continue;
                    }
                }

                String prevDiff;
                String validatedDiff = validateDirectionConsistency(isInc, currDiff);
                index += 2;

                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevDiff = currDiff;
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);

                    currDiff = findSafeDiff(prevLvl, currLvl);
                    validatedDiff = validateDirectionConsistency(isInc, currDiff);

                    if (validatedDiff == UNSAFE) {
                        if (index + 1 >= rowOfLevels.size()) {
                            // first UNSAFE is caused by the last index, so this is considered SAFE
                            validatedDiff = SAFE;
                        } else {
                            handleUnsafeReports(rowOfLevels, index, line, isInc, prevDiff, currDiff);
                        }

                        break;
                    }

                    index++;
                }

                // todo make sure to add to safeReports for the first handleUnsafeReport,
                //  since the current line will be skipped from this option
                if (validatedDiff != UNSAFE) {
                    incSafeReports(rowOfLevels);
                }
            }
        }

        int totalNumEntries = 1000;
        int nonTolerableSafeReports = 516;
        System.out.println("numEntries: " + numEntries + "; expected " + totalNumEntries);
//        System.out.println("Non-Tolerable numSafeReports: " + numSafeReports + "; expected " + nonTolerableSafeReports);
//        System.out.println("Non-Tolerable safeReports.size: " + safeReports.size() + "; expected " + nonTolerableSafeReports);
//        System.out.println("Non-Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (totalNumEntries - nonTolerableSafeReports));

        System.out.println("Tolerable numSafeReports: " + numSafeReports + "; expected ?");
        System.out.println("Tolerable safeReports.size: " + safeReports.size() + "; expected ?");
        System.out.println("Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (totalNumEntries - safeReports.size()));

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

    /**
     * This method handles unsafe reports caused by the first or middle indexes
     * @param rowOfLevels
     * @param currIndex
     * @param line
     * @param prevDiff
     * @param currDiff
     */
    private void handleUnsafeReports(
            List<Integer> rowOfLevels,
            int currIndex,
            String line,     // for sout
            Boolean givenIsInc,
            String prevDiff,
            String givenCurrDiff
    ) {
        System.out.println(line);

        // if first and last indexes are unsafe, then they should be considered safe
        // if middle indexes are unsafe...

        // todo remove unsafe level from list and continue checking, since two pointer is not working.
        //  new head/tail pointers are set incorrectly somehow

        // case: first to second index is unsafe
        if (prevDiff == null) {
            validateUnsafeReportOfFirstIndex(rowOfLevels, currIndex);
        } else {
            System.out.println("Handle middle indexes");

            int prevLvl;
            int currLvl;
            boolean secondIndexUnsafe = currIndex == 2;

            // note: prevDiff is the expected diff, since currDiff is invalid caused by get(i - 2) and get(i - 1).
            // thus, need to check if currIndex (i) is safe with prevPrevIndex (i - 2)

            if (secondIndexUnsafe) {
                prevLvl = rowOfLevels.get(currIndex);
            } else {
                prevLvl = rowOfLevels.get(currIndex - 1);
            }


            currIndex++;
            currLvl = rowOfLevels.get(currIndex);

            String currDiff = findSafeDiff(prevLvl, currLvl);
            boolean isInc;

            switch (currDiff) {
                case INCREASING -> isInc = true;
                case DECREASING -> isInc = false;
                default -> {
                    // at this point, two UNSAFE's thus return.
                    System.out.println("UNSAFE 3: " + currDiff);
                    this.unsafeReports.add(rowOfLevels);

                    return;
                }
            }

            // it is considered safe if the first direction change is a one-off,
            // so index > 3 with direction change is unsafe
            if (currIndex > 3) {
                if (isInc != givenIsInc) {
                    // at this point, two UNSAFE's thus return.
                    System.out.println("UNSAFE 4: [isInc=" + isInc + ", givenIsInc=" + givenIsInc + "]" );
                    this.unsafeReports.add(rowOfLevels);

                    return;
                }
            }

            // at this point, only one UNSAFE
            String validatedDiff = validateDirectionConsistency(isInc, currDiff);
            currIndex++;

            for (int i = currIndex; i < rowOfLevels.size(); i++) {
                prevLvl = currLvl;
                currLvl = rowOfLevels.get(i);

                currDiff = findSafeDiff(prevLvl, currLvl);
                validatedDiff = validateDirectionConsistency(isInc, currDiff);

                if (validatedDiff == UNSAFE) {
                    // at this point, two UNSAFE's thus return.
                    System.out.println("UNSAFE 5: " + validatedDiff);
                    this.unsafeReports.add(rowOfLevels);

                    return;
                }

                currIndex++;
            }

            if (validatedDiff != UNSAFE) {
                System.out.println("SAFE!");
                incSafeReports(rowOfLevels);
            }
        }
    }

    private void validateUnsafeReportOfFirstIndex(List<Integer> rowOfLevels, int currIndex) {
        // note at this point, currDiff either EQUAL or BIG_DIFF
        int prevLvl = rowOfLevels.get(currIndex);
        int currLvl = rowOfLevels.get(currIndex + 1);

        String currDiff = findSafeDiff(prevLvl, currLvl);
        boolean isInc;

        switch (currDiff) {
            case INCREASING -> isInc = true;
            case DECREASING -> isInc = false;
            default -> {
                // at this point, two UNSAFE's thus return.
                System.out.println("UNSAFE 1!");
                this.unsafeReports.add(rowOfLevels);

                return;
            }
        }

        // at this point, only one UNSAFE
        String validatedDiff = validateDirectionConsistency(isInc, currDiff);
        currIndex += 2;

        for (int i = currIndex; i < rowOfLevels.size(); i++) {
            prevLvl = currLvl;
            currLvl = rowOfLevels.get(i);

            currDiff = findSafeDiff(prevLvl, currLvl);
            validatedDiff = validateDirectionConsistency(isInc, currDiff);

            if (validatedDiff == UNSAFE) {
                // at this point, two UNSAFE's thus return.
                System.out.println("UNSAFE 2!");
                this.unsafeReports.add(rowOfLevels);

                return;
            }

            currIndex++;
        }

        if (validatedDiff != UNSAFE) {
            System.out.println("SAFE!");
            incSafeReports(rowOfLevels);
        }
    }

    private void incSafeReports(List<Integer> rowOfLevels) {
        this.safeReports.add(rowOfLevels);

        numSafeReports++;
    }
}
