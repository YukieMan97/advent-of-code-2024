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
    private List<List<Integer>> safeReports;
    private List<List<Integer>> unsafeReports;

    protected final static String POSITIVE = "positive";
    protected final static String NEGATIVE = "negative";
    protected final static String UNSAFE = "unsafe";

    public SafetyReportFinder() throws IOException {
        this.safeReports = new ArrayList<>();
        this.unsafeReports = new ArrayList<>();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");

        int prevLvl;
        int currLvl;

        assert inputStream != null;
        InputStreamReader inStreamReader = new InputStreamReader(inputStream);

        findSafeAndUnsafeReports(inStreamReader);

        System.out.println("Safe Reports: " + numSafeReports);
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

    private void findSafeAndUnsafeReports(InputStreamReader inStreamReader) throws IOException {
        int prevLvl;
        int currLvl;
        try (BufferedReader reader = new BufferedReader(inStreamReader)) {
            while (reader.ready()) {
                String line = reader.readLine();
                System.out.println(line);

                List<Integer> rowOfLevels = Arrays.stream(line.split("\\s"))
                        .map(Integer::parseInt)
                        .toList();

                // assumption: there are at least 5 levels
                prevLvl = rowOfLevels.getFirst();
                currLvl = rowOfLevels.get(1);

                String res = isIncSafeDiff(null, prevLvl, currLvl);
                boolean isInc;

                switch (res) {
                    case POSITIVE -> isInc = true;
                    case NEGATIVE -> isInc = false;
                    default -> {
                        continue;
                    }
                }

                for (int i = 2; i < rowOfLevels.size(); i++) {
                    prevLvl = currLvl;
                    currLvl = rowOfLevels.get(i);
                    res = isIncSafeDiff(isInc, prevLvl, currLvl);

                    if (res == UNSAFE) {
                        break;
                    }
                }

                if (res != UNSAFE) {
                    this.safeReports.add(rowOfLevels);

                    numSafeReports++;
                } else {
                    this.unsafeReports.add(rowOfLevels);
                }
            }
        }
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
