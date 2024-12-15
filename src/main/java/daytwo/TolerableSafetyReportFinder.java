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
        List<Integer> removeFirstIndexList = null;
        List<Integer> removeSecondIndexList = null;
        boolean seenUnsafe = false;

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
                seenUnsafe = true;

                // todo check if first and third indices are safe first?
                removeFirstIndexList = new ArrayList<>(rowOfLevels);
                removeFirstIndexList.remove(index);

                removeSecondIndexList = new ArrayList<>(rowOfLevels);
                removeSecondIndexList.remove(index + 1);
            }
        }

        if (isInc != null) {
            // todo check if the pattern stays the same for the next set
            int nextLvl = rowOfLevels.get(index + 2);

            String nextDiff = findSafeDiff(currLvl, nextLvl);
            Boolean isNextInc = null;

            switch (nextDiff) {
                case INCREASING -> isNextInc = true;
                case DECREASING -> isNextInc = false;
            }

            if (isNextInc == null) {
                seenUnsafe = true;

                // todo check if first and third indices are safe first?
                removeFirstIndexList = new ArrayList<>(rowOfLevels);
                removeFirstIndexList.remove(index);

                removeSecondIndexList = new ArrayList<>(rowOfLevels);
                removeSecondIndexList.remove(index + 1);
            } else {
                if (isInc != isNextInc) {
                    seenUnsafe = true;

                    // todo check if first and third indices are safe first?
                    removeFirstIndexList = new ArrayList<>(rowOfLevels);
                    removeFirstIndexList.remove(index);

                    removeSecondIndexList = new ArrayList<>(rowOfLevels);
                    removeSecondIndexList.remove(index + 1);
                }
            }
        }

        boolean checkSecondList = false;

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
                default ->
                    // at this point, removeFirstIndexList is not safe, so check removeSecondIndexList now
                    checkSecondList = true;
            }

            if (!checkSecondList) {
                // note: removeFirstIndexList is safe so far
                // note: prev implementation
                String validatedDiff2 = validateDirectionConsistency(isInc2, currDiff2);
                index2 += 2;

                for (int i = index2; i < removeFirstIndexList.size(); i++) {

                    // note: this is added for p2
//                    if (i + 1 > removeFirstIndexList.size() - 1) {
//                        // todo need to check if last will be unsafe
//                        // at this point, seenUnsafe should be true, thus CANNOT ignore last unsafe level
//                        assert seenUnsafe;
//
//                        System.out.println("1: " + removeFirstIndexList);
//                        return;
//                    }

                    prevLvl2 = currLvl2;
                    currLvl2 = removeFirstIndexList.get(i);

                    currDiff2 = findSafeDiff(prevLvl2, currLvl2);
                    validatedDiff2 = validateDirectionConsistency(isInc2, currDiff2);

                    if (validatedDiff2 == UNSAFE) {
                        // at this point, seenUnsafe should be true
                        assert seenUnsafe;

                        System.out.println("2: " + removeFirstIndexList);
                        return;
                    }

                    index2++;
                }

                if (validatedDiff2 != UNSAFE) {
                    // at this point, removeFirstIndexList is safe, so add the OG list to this.safeReports
                    this.safeReports.add(rowOfLevels);

                    // then no need to check the removeSecondIndexList
                    System.out.println("3: (safe) " + rowOfLevels);
                    return;
                }

                checkSecondList = true;
            }
        }

        if (checkSecondList) {
            // assumption: there are at least 5 levels
            int index3 = 0;
            int prevLvl3 = removeSecondIndexList.get(index3);
            int currLvl3 = removeSecondIndexList.get(index3 + 1);

            String currDiff3 = findSafeDiff(prevLvl3, currLvl3);
            boolean isInc3;

            switch (currDiff3) {
                case INCREASING -> isInc3 = true;
                case DECREASING -> isInc3 = false;
                default -> {
                    // note removeFirstIndexList and removeSecondIndexList are both unsafe

                    System.out.println("4: " + removeSecondIndexList);
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

                    System.out.println("5: " + rowOfLevels);
                    return;
                }

                prevLvl3 = currLvl3;
                currLvl3 = removeSecondIndexList.get(i);

                currDiff3 = findSafeDiff(prevLvl3, currLvl3);
                validatedDiff3 = validateDirectionConsistency(isInc3, currDiff3);

                if (validatedDiff3 == UNSAFE) {
                    // at this point, seenUnsafe should be true
                    assert seenUnsafe;

                    System.out.println("6: " + removeSecondIndexList);
                    return;
                }

                index3++;
            }

            if (validatedDiff3 != UNSAFE) {
                // at this point, removeSecondIndexList is safe, so add the OG list to this.safeReports
                this.safeReports.add(rowOfLevels);

                // then no need to check the rest of the OG list
                System.out.println("7 (safe): " + rowOfLevels);
                return;
            }
        }

        // At this point, the first and second indices are safe, check the rest of the indices for OG list
        // note: prev implementation
        String validatedDiff = validateDirectionConsistency(isInc, currDiff);
        index += 2;

        for (int i = index; i < rowOfLevels.size(); i++) {
            // note: this is added for p2
            if (i + 1 > rowOfLevels.size() - 1) {
                // can ignore checking the last unsafe level if not seen any unsafe levels yet
                // todo maybe can just iterate until second last element (change forloop break condition)
                if (seenUnsafe) {
                    System.out.println("8: " + rowOfLevels);
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
                    System.out.println("9: " + rowOfLevels);
                    return;
                } else {
                    seenUnsafe = true;

                    // todo check if prevLvl and nextLvl is ok. if ok, then increment index by 2
                    int nextLvl = rowOfLevels.get(index + 2);

                    validatedDiff = findSafeDiff(prevLvl, nextLvl);
                    Boolean isNextInc;

                    switch (validatedDiff) {
                        case INCREASING -> isNextInc = true;
                        case DECREASING -> isNextInc = false;
                        default -> {
                            System.out.println("10: " + rowOfLevels);

                            return;
                        }
                    }

                    if (isNextInc == isInc) {
                        index += 2;
                        i = index;

                        continue;
                    }
                }
            }

            index++;
        }

        if (validatedDiff != UNSAFE) {
            System.out.println("11: (safe) " + rowOfLevels);
            this.safeReports.add(rowOfLevels);
        }
    }

    public int getNumSafeReports() {
        return this.unsafeReports.size();
    }

}
