package simulator.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationValidationMetricsTest {
    @Test
    void pdfScenarioPassesWhenSimulatedMatchesAnalytical() {
        SimulationValidationMetrics validationMetrics = new SimulationValidationMetrics();
        ValidationReport report = validationMetrics.validatePdfScenario();

        assertEquals(0.667, report.getAnalyticalSystemUtilization(), 1e-3);
        assertEquals(2.0 / 9.0, report.getAnalyticalQueueWait(), 1e-3);
        assertTrue(report.isOverallValid());
    }

    @Test
    void relativeErrorIsZeroForMatchingValues() {
        assertEquals(0.0, MetricsEquations.relativeError(0.667, 0.667), 1e-9);
    }

    @Test
    void validationFlagsUtilizationMismatch() {
        MetricsReport simulatedReport = new MetricsReport();
        simulatedReport.setSimulatedShopTechnicianUtilization(0.9);
        simulatedReport.setAverageQueueDelay(2.0 / 9.0);
        simulatedReport.setAveragePartsDelay(0.0);
        simulatedReport.setAverageCustomerWaitTime(2.722);

        SimulationValidationMetrics validationMetrics = new SimulationValidationMetrics();
        ValidationReport report = validationMetrics.validateAgainstQueueBenchmark(
                simulatedReport,
                4.0,
                3,
                2.0,
                0.5,
                2.0,
                0.15);

        assertTrue(report.getUtilizationRelativeError() > 0.15);
        assertFalseUtilization(report);
    }

    private static void assertFalseUtilization(ValidationReport report) {
        org.junit.jupiter.api.Assertions.assertFalse(report.isUtilizationWithinTolerance());
        org.junit.jupiter.api.Assertions.assertFalse(report.isOverallValid());
    }
}
