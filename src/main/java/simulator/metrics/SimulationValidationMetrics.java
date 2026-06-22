package simulator.metrics;

public class SimulationValidationMetrics {
    public static final double DEFAULT_RELATIVE_TOLERANCE = 0.15;

    private final QueueBenchmarkMetrics queueBenchmarkMetrics = new QueueBenchmarkMetrics();

    public ValidationReport validateAgainstQueueBenchmark(
            MetricsReport simulatedReport,
            double arrivalRate,
            int technicianCount,
            double serviceRatePerTechnician,
            double averageAdvisorWaitTime,
            double averageServiceTime) {
        return validateAgainstQueueBenchmark(
                simulatedReport,
                arrivalRate,
                technicianCount,
                serviceRatePerTechnician,
                averageAdvisorWaitTime,
                averageServiceTime,
                DEFAULT_RELATIVE_TOLERANCE);
    }

    public ValidationReport validateAgainstQueueBenchmark(
            MetricsReport simulatedReport,
            double arrivalRate,
            int technicianCount,
            double serviceRatePerTechnician,
            double averageAdvisorWaitTime,
            double averageServiceTime,
            double relativeTolerance) {
        ValidationReport validationReport = new ValidationReport();
        validationReport.setValidationTolerance(relativeTolerance);

        double analyticalUtilization = queueBenchmarkMetrics.calculateSystemUtilization(
                arrivalRate,
                technicianCount,
                serviceRatePerTechnician);
        double analyticalQueueWait = queueBenchmarkMetrics.calculateExpectedQueueWait(
                arrivalRate,
                technicianCount,
                serviceRatePerTechnician);
        double analyticalCustomerWait = MetricsEquations.totalCustomerWaitTime(
                averageAdvisorWaitTime,
                analyticalQueueWait,
                simulatedReport.getAveragePartsDelay(),
                averageServiceTime);

        double simulatedUtilization = simulatedReport.getSimulatedShopTechnicianUtilization();
        double simulatedQueueWait = simulatedReport.getAverageQueueDelay();
        double simulatedCustomerWait = simulatedReport.getAverageCustomerWaitTime();

        validationReport.setSimulatedSystemUtilization(simulatedUtilization);
        validationReport.setAnalyticalSystemUtilization(analyticalUtilization);
        validationReport.setUtilizationRelativeError(
                MetricsEquations.relativeError(simulatedUtilization, analyticalUtilization));
        validationReport.setUtilizationWithinTolerance(
                MetricsEquations.withinRelativeTolerance(
                        simulatedUtilization,
                        analyticalUtilization,
                        relativeTolerance));

        validationReport.setSimulatedQueueWait(simulatedQueueWait);
        validationReport.setAnalyticalQueueWait(analyticalQueueWait);
        validationReport.setQueueWaitRelativeError(
                MetricsEquations.relativeError(simulatedQueueWait, analyticalQueueWait));
        validationReport.setQueueWaitWithinTolerance(
                MetricsEquations.withinRelativeTolerance(
                        simulatedQueueWait,
                        analyticalQueueWait,
                        relativeTolerance));

        validationReport.setSimulatedCustomerWait(simulatedCustomerWait);
        validationReport.setAnalyticalCustomerWaitEstimate(analyticalCustomerWait);
        validationReport.setCustomerWaitRelativeError(
                MetricsEquations.relativeError(simulatedCustomerWait, analyticalCustomerWait));
        validationReport.setCustomerWaitWithinTolerance(
                MetricsEquations.withinRelativeTolerance(
                        simulatedCustomerWait,
                        analyticalCustomerWait,
                        relativeTolerance));

        validationReport.setOverallValid(
                validationReport.isUtilizationWithinTolerance()
                        && validationReport.isQueueWaitWithinTolerance()
                        && validationReport.isCustomerWaitWithinTolerance());
        return validationReport;
    }

    public ValidationReport validatePdfScenario() {
        MetricsReport scenarioReport = new MetricsReport();
        scenarioReport.setSimulatedShopTechnicianUtilization(0.667);
        scenarioReport.setAverageQueueDelay(0.222);
        scenarioReport.setAveragePartsDelay(0.0);
        scenarioReport.setAverageCustomerWaitTime(2.722);

        return validateAgainstQueueBenchmark(
                scenarioReport,
                4.0,
                3,
                2.0,
                0.5,
                2.0,
                DEFAULT_RELATIVE_TOLERANCE);
    }
}
