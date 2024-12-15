package daytwo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static daytwo.DayTwoUtil.*;

/**
 * Day2 Part1
 */
public class SafetyReportFinder {
    private final List<List<Integer>> safeReports;
    private final List<List<Integer>> unsafeReports;

    public SafetyReportFinder() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("v_d2_input.txt");
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input_tests.txt");

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

    private void findSafeAndUnsafeReports(InputStreamReader inStreamReader) throws Exception {
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
                prevLvl = rowOfLevels.get(index);
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

        int nonTolerableSafeReports = 516;
        System.out.println("numEntries: " + numEntries);
//        System.out.println("Non-Tolerable safeReports.size: " + safeReports.size() + "; expected " + nonTolerableSafeReports);
//        System.out.println("Non-Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (totalNumEntries - nonTolerableSafeReports));

        System.out.println("Tolerable safeReports.size: " + safeReports.size());
        System.out.println("Tolerable unsafeReports.size: " + unsafeReports.size() + "; expected " + (numEntries - safeReports.size()));
    }
}
