package daytwo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static daytwo.DayTwoUtil.*;

/**
 * Day2 Part2
 */
public class TolerableSafetyReportFinder extends SafetyReportFinder {
    private List<List<Integer>> safeReports;
    private List<List<Integer>> unsafeReports;
    // todo consider which of the following fields to keep
    private boolean seenUnsafe = false;
    private Boolean tolerableSafeReport = null;
    private Boolean isInc = null;
    private String prevToNextLvlDiff = null;
    private String currToNextLvlDiff = null;

    public TolerableSafetyReportFinder(
        List<List<Integer>> safeReports,
        List<List<Integer>> unsafeReports
    ) throws IOException {
         this.safeReports = safeReports;
         this.unsafeReports = unsafeReports;

         List<Integer> rowOfLevels;

         for (int i = 0; i < unsafeReports.size(); i++) {
             rowOfLevels = unsafeReports.get(i);

             handleUnsafeReport(rowOfLevels);
         }
    }

    private void handleUnsafeReport(List<Integer> rowOfLevels) {
        this.seenUnsafe = false;
        this.tolerableSafeReport = null;
        this.isInc = null;
        this.prevToNextLvlDiff = null;
        this.currToNextLvlDiff = null;

        // assumption: there are at least 5 levels
        int index = 0;
        int prevLvl = rowOfLevels.getFirst();
        int currLvl = rowOfLevels.get(index + 1);

        String currDiff = findSafeDiff(prevLvl, currLvl);
        boolean isInc;

        switch (currDiff) {
            case INCREASING -> isInc = true;
            case DECREASING -> isInc = false;
            default -> {
                this.unsafeReports.add(rowOfLevels);

                return;
            }
        }

        String validatedDiff = validateDirectionConsistency(isInc, currDiff);
        index += 2;

        for (int i = index; i < rowOfLevels.size(); i++) {

            // note: this is added for p2
            if (i + 1 > rowOfLevels.size() - 1) {
                // can ignore last unsafe level
                break;
            }

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

    // todo not use in p2
    private int findIncOrDec(
        int currIndex,
        int prevLvl,
        int currLvl,
        List<Integer> rowOfLevels,
        String currDiff
    ) {
        List<Integer> prevLvlRemovedList;
        List<Integer> currLvlRemovedList;

        switch (currDiff) {
            case INCREASING -> this.isInc = true;
            case DECREASING -> this.isInc = false;
            default -> {
                if (this.seenUnsafe) {
                    break;
                }

                this.seenUnsafe = true;

                int nextLvl = rowOfLevels.get(currIndex + 1);

                prevToNextLvlDiff = findSafeDiff(prevLvl, nextLvl);

                switch (prevToNextLvlDiff) {
                    case INCREASING -> this.isInc = true;
                    case DECREASING -> this.isInc = false;
                    default -> {
                        currToNextLvlDiff = findSafeDiff(currLvl, nextLvl);

                        switch (currToNextLvlDiff) {
                            case INCREASING -> this.isInc = true;
                            case DECREASING -> this.isInc = false;
                            default -> {
                                // need to remove both levels, thus unsafe report
                                // todo break
                            }
                        }

                        currLvlRemovedList = new ArrayList<>(rowOfLevels);
                        currLvlRemovedList.remove(currIndex);

                        // todo iterate over currLvlRemovedList
                    }
                }

                prevLvlRemovedList = new ArrayList<>(rowOfLevels);
                prevLvlRemovedList.remove(currIndex - 1);

                // todo iterate over prevLvlRemovedList;
            }
        }

        return 0;
    }

    public int getNumSafeReports() {
        return this.unsafeReports.size();
    }

}
