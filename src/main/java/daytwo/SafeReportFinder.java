package daytwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Day 2 Part 1
 */
public class SafeReportFinder {
    private final static String POSITIVE = "positive";
    private final static String NEGATIVE = "negative";
    private final static String UNSAFE = "unsafe";

    public static void main(String[] args) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("d2_input.txt");

        int prevNum;
        int currNum;
        int safeReports = 0;

        assert inputStream != null;
        InputStreamReader inStreamReader = new InputStreamReader(inputStream);

        try (BufferedReader reader = new BufferedReader(inStreamReader)) {
            while (reader.ready()) {
                String line = reader.readLine();
                System.out.println(line);

                List<Integer> levels = Arrays.stream(line.split("\\s"))
                        .map(Integer::parseInt)
                        .toList();

                // assumption: there are at least 5 levels
                prevNum = levels.getFirst();
                currNum = levels.get(1);

                String res = isIncSafeDiff(null, prevNum, currNum);
                boolean isInc;

                switch (res) {
                    case POSITIVE -> isInc = true;
                    case NEGATIVE -> isInc = false;
                    default -> {
                        continue;
                    }
                }

                for (int i = 2; i < levels.size(); i++) {
                    prevNum = currNum;
                    currNum = levels.get(i);
                    res = isIncSafeDiff(isInc, prevNum, currNum);

                    if (res == UNSAFE) {
                        break;
                    }
                }

                if (res != UNSAFE) {
                    safeReports++;
                }
            }
        }

        System.out.println("Safe Reports: " + safeReports);
    }

    public static String isIncSafeDiff(Boolean givenInc, int prevNum, int currNum) {
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

    public static String findSafeDiff(int prevNum, int currNum) {
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
