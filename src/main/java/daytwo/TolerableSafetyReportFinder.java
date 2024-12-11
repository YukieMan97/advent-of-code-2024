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

    private boolean seenUnsafe = false;
    private Boolean tolerableSafeReport = null;
    private Boolean isInc = null;
    private String prevToNextLvlDiff = null;
    private String currToNextLvlDiff = null;

    private void handleUnsafeReport(List<Integer> rowOfLevels) throws Exception {
        this.seenUnsafe = false;
        this.tolerableSafeReport = null;
        this.isInc = null;
        this.prevToNextLvlDiff = null;
        this.currToNextLvlDiff = null;

        int prevLvl;
        int currLvl;
        String currDiff = null;

        for (int j = 1; j < rowOfLevels.size(); j++) {
            prevLvl = rowOfLevels.get(j - 1);
            currLvl = rowOfLevels.get(j);

            currDiff = findSafeDiff(prevLvl, currLvl);

            if (j + 1 > rowOfLevels.size() - 1) {
                // can ignore last unsafe level
                break;
            }

            // todo this part needs to be reworked to use the updated removedList
            findIncOrDec(
                j,
                prevLvl,
                currLvl,
                rowOfLevels,
                currDiff
            );

            if (this.isInc == null) {
                this.unsafeReports.add(rowOfLevels);

                return;
            }

            String validatedDiff;

            if (this.currToNextLvlDiff != null) {
                validatedDiff = validateDirectionConsistency(this.isInc, currToNextLvlDiff);
            } else if (this.prevToNextLvlDiff != null) {
                validatedDiff = validateDirectionConsistency(this.isInc, prevToNextLvlDiff);
            } else {
                validatedDiff = validateDirectionConsistency(this.isInc, currDiff);
            }

            if (validatedDiff == UNSAFE) {
                if (this.seenUnsafe) {
                    this.tolerableSafeReport = false;
                    break;
                }
            }

        }

        if (this.tolerableSafeReport != null) {
            if (this.tolerableSafeReport) {
                this.safeReports.add(rowOfLevels);
            } else {
                this.unsafeReports.add(rowOfLevels);
            }
        } else {
            throw new Exception("Non-deterministic SafeReport");
        }

    }

    private int findIncOrDec(
        int currIndex,
        int prevLvl,
        int currLvl,
        List<Integer> rowOfLevels,
        String currDiff
    ) throws Exception {
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
    }

    public int getNumSafeReports() {
        return this.unsafeReports.size();
    }

}
