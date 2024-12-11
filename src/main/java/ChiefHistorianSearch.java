import daytwo.SafetyReportFinder;
import daytwo.TolerableSafetyReportFinder;

import java.io.IOException;

public class ChiefHistorianSearch {
    public static void main(String[] args) throws IOException {
        SafetyReportFinder safeReportFinder = new SafetyReportFinder();

        TolerableSafetyReportFinder tolerableSafeReportFinder = new TolerableSafetyReportFinder(
                safeReportFinder.getNumSafeReports(),
                safeReportFinder.getUnsafeReports()
        );
    }
}
