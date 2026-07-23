package simulator.analysis;

import simulator.metrics.MetricsReport;
import simulator.metrics.ValidationReport;

public final class ReplicationResult {
    private final int replicationId;
    private final long seed;
    private final long wallClockMillis;
    private final String experimentLabel;
    private final String paramsChanged;
    private final double averageCustomerWaitHours;
    private final double averageAdvisorWaitHours;
    private final double averageQueueDelayHours;
    private final double averagePartsDelayHours;
    private final double averageServiceTimeHours;
    private final double shopTechnicianUtilization;
    private final double shopBayUtilization;
    private final int jobsCompletedPerDay;
    private final double analyticalSystemUtilization;
    private final double analyticalQueueWaitHours;
    private final double utilizationRelativeError;
    private final double queueWaitRelativeError;
    private final boolean validationOverallValid;

    public ReplicationResult(
            int replicationId,
            long seed,
            long wallClockMillis,
            String experimentLabel,
            String paramsChanged,
            MetricsReport report) {
        this.replicationId = replicationId;
        this.seed = seed;
        this.wallClockMillis = wallClockMillis;
        this.experimentLabel = experimentLabel;
        this.paramsChanged = paramsChanged;
        this.averageCustomerWaitHours = report.getAverageCustomerWaitTime();
        this.averageAdvisorWaitHours = report.getAverageAdvisorWaitTime();
        this.averageQueueDelayHours = report.getAverageQueueDelay();
        this.averagePartsDelayHours = report.getAveragePartsDelay();
        this.averageServiceTimeHours = report.getAverageServiceTime();
        this.shopTechnicianUtilization = report.getSimulatedShopTechnicianUtilization();
        this.shopBayUtilization = report.getSimulatedShopBayUtilization();
        this.jobsCompletedPerDay = report.getJobsCompletedPerDay();
        this.analyticalSystemUtilization = report.getAnalyticalSystemUtilization();
        this.analyticalQueueWaitHours = report.getAnalyticalQueueWait();
        ValidationReport validation = report.getValidationReport();
        if (validation != null) {
            this.utilizationRelativeError = validation.getUtilizationRelativeError();
            this.queueWaitRelativeError = validation.getQueueWaitRelativeError();
            this.validationOverallValid = validation.isOverallValid();
        } else {
            this.utilizationRelativeError = Double.NaN;
            this.queueWaitRelativeError = Double.NaN;
            this.validationOverallValid = false;
        }
    }

    public static String csvHeader() {
        return "experiment,params_changed,replication_id,seed,wall_clock_ms,"
                + "avg_customer_wait_h,avg_advisor_wait_h,avg_queue_delay_h,avg_parts_delay_h,"
                + "avg_service_time_h,shop_tech_util,shop_bay_util,jobs_completed,"
                + "analytical_rho,analytical_wq_h,util_rel_error,wq_rel_error,validation_ok";
    }

    public String toCsvRow() {
        return String.format(
                java.util.Locale.US,
                "%s,%s,%d,%d,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.6f,%.6f,%s",
                escape(experimentLabel),
                escape(paramsChanged),
                replicationId,
                seed,
                wallClockMillis,
                averageCustomerWaitHours,
                averageAdvisorWaitHours,
                averageQueueDelayHours,
                averagePartsDelayHours,
                averageServiceTimeHours,
                shopTechnicianUtilization,
                shopBayUtilization,
                jobsCompletedPerDay,
                analyticalSystemUtilization,
                analyticalQueueWaitHours,
                utilizationRelativeError,
                queueWaitRelativeError,
                validationOverallValid);
    }

    private static String escape(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return '"' + field.replace("\"", "\"\"") + '"';
        }
        return field;
    }

    public int getReplicationId() {
        return replicationId;
    }

    public long getSeed() {
        return seed;
    }

    public String getExperimentLabel() {
        return experimentLabel;
    }

    public String getParamsChanged() {
        return paramsChanged;
    }

    public double getAverageCustomerWaitHours() {
        return averageCustomerWaitHours;
    }

    public double getAverageAdvisorWaitHours() {
        return averageAdvisorWaitHours;
    }

    public double getAverageQueueDelayHours() {
        return averageQueueDelayHours;
    }

    public double getAveragePartsDelayHours() {
        return averagePartsDelayHours;
    }

    public double getAverageServiceTimeHours() {
        return averageServiceTimeHours;
    }

    public double getShopTechnicianUtilization() {
        return shopTechnicianUtilization;
    }

    public double getShopBayUtilization() {
        return shopBayUtilization;
    }

    public int getJobsCompletedPerDay() {
        return jobsCompletedPerDay;
    }

    public double getAnalyticalSystemUtilization() {
        return analyticalSystemUtilization;
    }

    public double getAnalyticalQueueWaitHours() {
        return analyticalQueueWaitHours;
    }

    public double getUtilizationRelativeError() {
        return utilizationRelativeError;
    }

    public double getQueueWaitRelativeError() {
        return queueWaitRelativeError;
    }

    public boolean isValidationOverallValid() {
        return validationOverallValid;
    }

    public long getWallClockMillis() {
        return wallClockMillis;
    }
}
