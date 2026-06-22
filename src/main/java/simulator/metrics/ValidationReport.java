package simulator.metrics;

public class ValidationReport {
    private double simulatedSystemUtilization;
    private double analyticalSystemUtilization;
    private double utilizationRelativeError;
    private boolean utilizationWithinTolerance;

    private double simulatedQueueWait;
    private double analyticalQueueWait;
    private double queueWaitRelativeError;
    private boolean queueWaitWithinTolerance;

    private double simulatedCustomerWait;
    private double analyticalCustomerWaitEstimate;
    private double customerWaitRelativeError;
    private boolean customerWaitWithinTolerance;

    private double validationTolerance;
    private boolean overallValid;

    public double getSimulatedSystemUtilization() {
        return simulatedSystemUtilization;
    }

    public void setSimulatedSystemUtilization(double simulatedSystemUtilization) {
        this.simulatedSystemUtilization = simulatedSystemUtilization;
    }

    public double getAnalyticalSystemUtilization() {
        return analyticalSystemUtilization;
    }

    public void setAnalyticalSystemUtilization(double analyticalSystemUtilization) {
        this.analyticalSystemUtilization = analyticalSystemUtilization;
    }

    public double getUtilizationRelativeError() {
        return utilizationRelativeError;
    }

    public void setUtilizationRelativeError(double utilizationRelativeError) {
        this.utilizationRelativeError = utilizationRelativeError;
    }

    public boolean isUtilizationWithinTolerance() {
        return utilizationWithinTolerance;
    }

    public void setUtilizationWithinTolerance(boolean utilizationWithinTolerance) {
        this.utilizationWithinTolerance = utilizationWithinTolerance;
    }

    public double getSimulatedQueueWait() {
        return simulatedQueueWait;
    }

    public void setSimulatedQueueWait(double simulatedQueueWait) {
        this.simulatedQueueWait = simulatedQueueWait;
    }

    public double getAnalyticalQueueWait() {
        return analyticalQueueWait;
    }

    public void setAnalyticalQueueWait(double analyticalQueueWait) {
        this.analyticalQueueWait = analyticalQueueWait;
    }

    public double getQueueWaitRelativeError() {
        return queueWaitRelativeError;
    }

    public void setQueueWaitRelativeError(double queueWaitRelativeError) {
        this.queueWaitRelativeError = queueWaitRelativeError;
    }

    public boolean isQueueWaitWithinTolerance() {
        return queueWaitWithinTolerance;
    }

    public void setQueueWaitWithinTolerance(boolean queueWaitWithinTolerance) {
        this.queueWaitWithinTolerance = queueWaitWithinTolerance;
    }

    public double getSimulatedCustomerWait() {
        return simulatedCustomerWait;
    }

    public void setSimulatedCustomerWait(double simulatedCustomerWait) {
        this.simulatedCustomerWait = simulatedCustomerWait;
    }

    public double getAnalyticalCustomerWaitEstimate() {
        return analyticalCustomerWaitEstimate;
    }

    public void setAnalyticalCustomerWaitEstimate(double analyticalCustomerWaitEstimate) {
        this.analyticalCustomerWaitEstimate = analyticalCustomerWaitEstimate;
    }

    public double getCustomerWaitRelativeError() {
        return customerWaitRelativeError;
    }

    public void setCustomerWaitRelativeError(double customerWaitRelativeError) {
        this.customerWaitRelativeError = customerWaitRelativeError;
    }

    public boolean isCustomerWaitWithinTolerance() {
        return customerWaitWithinTolerance;
    }

    public void setCustomerWaitWithinTolerance(boolean customerWaitWithinTolerance) {
        this.customerWaitWithinTolerance = customerWaitWithinTolerance;
    }

    public double getValidationTolerance() {
        return validationTolerance;
    }

    public void setValidationTolerance(double validationTolerance) {
        this.validationTolerance = validationTolerance;
    }

    public boolean isOverallValid() {
        return overallValid;
    }

    public void setOverallValid(boolean overallValid) {
        this.overallValid = overallValid;
    }
}
