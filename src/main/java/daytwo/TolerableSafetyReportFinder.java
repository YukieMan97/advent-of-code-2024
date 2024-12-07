package daytwo;

import java.io.IOException;
import java.util.List;

/**
 * Day2 Part2
 */
public class TolerableSafetyReportFinder extends SafetyReportFinder {
    private int numSafeReports;
    private List<List<Integer>> safeReports;
    private List<List<Integer>> unsafeReports;

    public TolerableSafetyReportFinder(
        int numSafeReports,
        List<List<Integer>> safeReports,
        List<List<Integer>> unsafeReports
    ) throws IOException {
         this.numSafeReports = numSafeReports;
         this.safeReports = safeReports;
         this.unsafeReports = unsafeReports;
    }


}
