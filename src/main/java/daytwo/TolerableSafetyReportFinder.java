package daytwo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Day2 Part2
 */
public class TolerableSafetyReportFinder extends SafetyReportFinder {
    private int numSafeReports;
    private List<List<Integer>> safeReports;
    private Map<List<Integer>, Integer> unsafeLvlToReports;

    public TolerableSafetyReportFinder(
        int numSafeReports,
        List<List<Integer>> safeReports,
        Map<List<Integer>, Integer> unsafeLvlToReports
    ) throws IOException {
         this.numSafeReports = numSafeReports;
         this.safeReports = safeReports;
         this.unsafeLvlToReports = unsafeLvlToReports;

         for (int i = 0; i < unsafeLvlToReports.size(); i++) {

         }
    }


}
