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
    ) throws Exception {
         this.safeReports = safeReports;
         this.unsafeReports = unsafeReports;

         List<Integer> rowOfLevels;

         for (int i = 0; i < unsafeReports.size(); i++) {
             rowOfLevels = unsafeReports.get(i);

             handleUnsafeReport(rowOfLevels);
         }

        System.out.println("tolerableSafeReports: " + safeReports.size());
    }

    private void handleUnsafeReport(List<Integer> rowOfLevels) throws Exception {
        this.seenUnsafe = false;
        this.tolerableSafeReport = null;
        this.isInc = null;
        this.prevToNextLvlDiff = null;
        this.currToNextLvlDiff = null;

        List<Integer> removeFirstIndexList = null;
        List<Integer> removeSecondIndexList = null;

        // assumption: there are at least 5 levels
        int index = 0;
        int prevLvl = rowOfLevels.get(index);
        int currLvl = rowOfLevels.get(index + 1);

        String currDiff = findSafeDiff(prevLvl, currLvl);
        Boolean isInc = null;

        switch (currDiff) {
            case INCREASING -> isInc = true;
            case DECREASING -> isInc = false;
            default -> {
                // note: prev implementation
//                this.unsafeReports.add(rowOfLevels);

                // todo
                //  1. set seenUnsafe = true
                seenUnsafe = true;

                // todo
                //  2. create two lists
                //      - one removes the first index
                //      - one removes the second index

                removeFirstIndexList = new ArrayList<>(rowOfLevels);
                removeFirstIndexList.remove(index);

                removeSecondIndexList = new ArrayList<>(rowOfLevels);
                removeSecondIndexList.remove(index);
            }
        }

        // todo
        //  3. check which list is safe
        //      - if at least one is safe, add the OG list to this.safeReports,
        //          otherwise, add the OG list to this.unsafeReports

        boolean checkSecondList = false;

        // todo check the first list
        if (removeFirstIndexList != null) {
            // assumption: there are at least 5 levels
            int index2 = 0;
            int prevLvl2 = removeFirstIndexList.get(index2);
            int currLvl2 = removeFirstIndexList.get(index2 + 1);

            String currDiff2 = findSafeDiff(prevLvl2, currLvl2);
            Boolean isInc2 = null;

            switch (currDiff2) {
                case INCREASING -> isInc2 = true;
                case DECREASING -> isInc2 = false;
                default -> {
                    // todo removeFirstIndexList is not safe, so check removeSecondIndexList now
                    checkSecondList = true;
                }
            }

            if (!checkSecondList) {
                // note: removeFirstIndexList is safe so far
                // note: prev implementation
                String validatedDiff2 = validateDirectionConsistency(isInc2, currDiff2);
                index2 += 2;

                for (int i = index2; i < removeFirstIndexList.size(); i++) {

                    // note: this is added for p2
                    if (i + 1 > removeFirstIndexList.size() - 1) {
                        // at this point, seenUnsafe should be true, thus CANNOT ignore last unsafe level
                        assert seenUnsafe;

                        return;
                    }

                    prevLvl2 = currLvl2;
                    currLvl2 = removeFirstIndexList.get(i);

                    currDiff2 = findSafeDiff(prevLvl2, currLvl2);
                    validatedDiff2 = validateDirectionConsistency(isInc2, currDiff2);

                    if (validatedDiff2 == UNSAFE) {
                        // at this point, seenUnsafe should be true
                        assert seenUnsafe;

                        return;
                    }

                    index2++;
                }

                if (validatedDiff2 != UNSAFE) {
                    // at this point, removeFirstIndexList is safe, so add the OG list to this.safeReports
                    this.safeReports.add(rowOfLevels);

                    // then no need to check the removeSecondIndexList
                    return;
                }

                checkSecondList = true;
            }
        }

        // todo check the second list if needed
        if (checkSecondList) {
            // assumption: there are at least 5 levels
            int index3 = 0;
            int prevLvl3 = removeFirstIndexList.get(index3);
            int currLvl3 = removeFirstIndexList.get(index3 + 1);

            String currDiff3 = findSafeDiff(prevLvl3, currLvl3);
            boolean isInc3;

            switch (currDiff3) {
                case INCREASING -> isInc3 = true;
                case DECREASING -> isInc3 = false;
                default -> {
                    // note removeFirstIndexList and removeSecondIndexList are both unsafe

                    return;
                }
            }

            // note: removeSecondIndexList is safe so far
            // note: prev implementation
            String validatedDiff3 = validateDirectionConsistency(isInc3, currDiff3);
            index3 += 2;

            for (int i = index3; i < removeSecondIndexList.size(); i++) {

                // note: this is added for p2
                if (i + 1 > removeSecondIndexList.size() - 1) {
                    // at this point, seenUnsafe should be true, thus CANNOT ignore last unsafe level
                    assert seenUnsafe;

                    return;
                }

                prevLvl3 = currLvl3;
                currLvl3 = removeSecondIndexList.get(i);

                currDiff3 = findSafeDiff(prevLvl3, currLvl3);
                validatedDiff3 = validateDirectionConsistency(isInc3, currDiff3);

                if (validatedDiff3 == UNSAFE) {
                    // at this point, seenUnsafe should be true
                    assert seenUnsafe;

                    return;
                }

                index3++;
            }

            if (validatedDiff3 != UNSAFE) {
                // at this point, removeSecondIndexList is safe, so add the OG list to this.safeReports
                this.safeReports.add(rowOfLevels);

                // then no need to check the rest of the OG list
                return;
            }
        }

        // todo at this point, the first and second indices are safe, check the rest of the indices for OG list
        // note: prev implementation
        String validatedDiff = validateDirectionConsistency(isInc, currDiff);
        index += 2;

        for (int i = index; i < rowOfLevels.size(); i++) {

            // note: this is added for p2
            if (i + 1 > rowOfLevels.size() - 1) {
                // can ignore checking the last unsafe level if not seen any unsafe levels yet
                if (seenUnsafe) {
                    return;
                }

                break;
            }

            prevLvl = currLvl;
            currLvl = rowOfLevels.get(i);

            currDiff = findSafeDiff(prevLvl, currLvl);
            validatedDiff = validateDirectionConsistency(isInc, currDiff);

            if (validatedDiff == UNSAFE) {
                // can ignore middle unsafe level if not seen any unsafe levels yet
                if (seenUnsafe) {
                    return;
                }
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
