package daytwo;

public class DayTwoUtil {
    protected final static String INCREASING = "POSITIVE";
    protected final static String DECREASING = "NEGATIVE";
    protected final static String EQUAL = "EQUAL";
    protected final static String BIG_DIFF = "BIG_DIFF";
    protected final static String UNSAFE = "UNSAFE";
    protected final static String SAFE = "SAFE";

    /**
     * Safe Difference satisfies the following criteria:
     * (1) Difference is increasing or decreasing
     * (2) Inclusive difference of 1-3
     *
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
     * A valid direction satisfies means the levels are only increasing xor only decreasing
     *
     * @param isInc
     * @param resultDiff
     * @return
     */
    protected static String validateDirectionConsistency(Boolean isInc, String resultDiff) throws Exception {
        if (isInc == null) {
            throw new Exception("isInc should not be null");
        }

        if (isInc && resultDiff == INCREASING) {
            return INCREASING;
        }

        if (!isInc && resultDiff == DECREASING) {
            return DECREASING;
        }

        return UNSAFE;
    }
}
