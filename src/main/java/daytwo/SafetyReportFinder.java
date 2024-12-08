package daytwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Day2 Part1
 */
public class SafetyReportFinder {
    private int numSafeReports = 0;
    private final List<List<Integer>> safeReports;
    // assumption: each row is distinct
    private final Map<List<Integer>, Integer> unsafeLvlToReports;

    protected final static String POSITIVE = "positive";
    protected final static String NEGATIVE = "negative";
    protected final static String UNSAFE = "unsafe";

    public SafetyReportFinder() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");

        assert inputStream != null;
        InputStreamReader inStreamReader = new InputStreamReader(inputStream);

        this.safeReports = new ArrayList<>();
        this.unsafeLvlToReports = new HashMap<>();
        this.numSafeReports = findSafeAndUnsafeReports(inStreamReader);
    }

    public int getNumSafeReports() {
        return numSafeReports;
    }

    public Map<List<Integer>, Integer> getUnsafeLvlToReports() {
        return unsafeLvlToReports;
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
                System.out.println(line);

                List<Integer> rowOfLevels = Arrays.stream(line.split("\\s"))
                        .map(Integer::parseInt)
                        .toList();

                // assumption: there are at least 5 levels
                int index = 1;
                prevLvl = rowOfLevels.getFirst();
                currLvl = rowOfLevels.get(index);

                String res = isIncSafeDiff(null, prevLvl, currLvl);
                boolean isInc;

                switch (res) {
                    case POSITIVE -> isInc = true;
                    case NEGATIVE -> isInc = false;
                    default -> {
                        this.unsafeLvlToReports.put(rowOfLevels, index);

                        continue;
                    }
                }

                index = 2;
                for (int i = index; i < rowOfLevels.size(); i++) {
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);
                    res = isIncSafeDiff(isInc, prevLvl, currLvl);

                    if (res == UNSAFE) {
                        this.unsafeLvlToReports.put(rowOfLevels, index);

                        break;
                    }

                    index++;
                }

                if (res != UNSAFE) {
                    this.safeReports.add(rowOfLevels);

                    numSafeReports++;
                } else {
                    this.unsafeLvlToReports.put(rowOfLevels, index);
                }
            }
        }

        System.out.println("numEntries: " + numEntries);
        System.out.println("numSafeReports: " + numSafeReports);
        System.out.println("safeReports.size: " + safeReports.size());
        System.out.println("unsafeReports.size: " + unsafeLvlToReports.size());

        return numSafeReports;
    }

    protected static String isIncSafeDiff(Boolean givenInc, int prevNum, int currNum) {
        Boolean isInc = givenInc;
        String diffRes = findSafeDiff(prevNum, currNum);

        if (isInc == null) {
            switch (diffRes) {
                case POSITIVE -> isInc = true;
                case NEGATIVE -> isInc = false;
                default -> {
                    return UNSAFE;
                }
            }
        }

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

        if (diff >= 1 && diff <= 3) {
            return POSITIVE;
        }

        if (diff >= -3 && diff <= -1) {
            return NEGATIVE;
        }

        return UNSAFE;
    }
}
