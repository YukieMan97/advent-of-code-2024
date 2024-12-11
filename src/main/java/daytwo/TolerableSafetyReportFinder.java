package daytwo;

import java.io.IOException;
import java.util.List;

/**
 * Day2 Part2
 */
public class TolerableSafetyReportFinder extends SafetyReportFinder {
    private int numSafeReports;
    private List<List<Integer>> unsafeReports;

    public TolerableSafetyReportFinder(
        int numSafeReports,
        List<List<Integer>> unsafeReports
    ) throws IOException {
         this.numSafeReports = numSafeReports;
         this.unsafeReports = unsafeReports;

         for (int i = 0; i < unsafeReports.size(); i++) {

         }
    }


}
