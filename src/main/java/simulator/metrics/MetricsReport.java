package simulator.metrics;

import simulator.model.RepairBay;
import simulator.model.Technician;

import java.util.Collections;
import java.util.Map;

public class MetricsReport {
    private double averageCustomerWaitTime;
    private double averageAdvisorWaitTime;
    private double averageQueueDelay;
    private double averagePartsDelay;
    private double averageServiceTime;
    private double averageTotalJobDelay;
    private double analyticalQueueWait;
    private double analyticalSystemUtilization;
    private double simulatedShopTechnicianUtilization;
    private double simulatedShopBayUtilization;
    private int jobsCompletedPerDay;
    private Map<Technician, Double> technicianUtilization;
    private Map<Technician, Integer> jobsCompletedByTechnician;
    private Map<RepairBay, Double> bayUtilization;
    private ValidationReport validationReport;

    public double getAverageCustomerWaitTime() {
        return averageCustomerWaitTime;
    }

    public void setAverageCustomerWaitTime(double averageCustomerWaitTime) {
        this.averageCustomerWaitTime = averageCustomerWaitTime;
    }

    public double getAverageAdvisorWaitTime() {
        return averageAdvisorWaitTime;
    }

    public void setAverageAdvisorWaitTime(double averageAdvisorWaitTime) {
        this.averageAdvisorWaitTime = averageAdvisorWaitTime;
    }

    public double getAverageQueueDelay() {
        return averageQueueDelay;
    }

    public void setAverageQueueDelay(double averageQueueDelay) {
        this.averageQueueDelay = averageQueueDelay;
    }

    public double getAveragePartsDelay() {
        return averagePartsDelay;
    }

    public void setAveragePartsDelay(double averagePartsDelay) {
        this.averagePartsDelay = averagePartsDelay;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public double getAverageTotalJobDelay() {
        return averageTotalJobDelay;
    }

    public void setAverageTotalJobDelay(double averageTotalJobDelay) {
        this.averageTotalJobDelay = averageTotalJobDelay;
    }

    public double getAnalyticalQueueWait() {
        return analyticalQueueWait;
    }

    public void setAnalyticalQueueWait(double analyticalQueueWait) {
        this.analyticalQueueWait = analyticalQueueWait;
    }

    public double getAnalyticalSystemUtilization() {
        return analyticalSystemUtilization;
    }

    public void setAnalyticalSystemUtilization(double analyticalSystemUtilization) {
        this.analyticalSystemUtilization = analyticalSystemUtilization;
    }

    public double getSimulatedShopTechnicianUtilization() {
        return simulatedShopTechnicianUtilization;
    }

    public void setSimulatedShopTechnicianUtilization(double simulatedShopTechnicianUtilization) {
        this.simulatedShopTechnicianUtilization = simulatedShopTechnicianUtilization;
    }

    public double getSimulatedShopBayUtilization() {
        return simulatedShopBayUtilization;
    }

    public void setSimulatedShopBayUtilization(double simulatedShopBayUtilization) {
        this.simulatedShopBayUtilization = simulatedShopBayUtilization;
    }

    public int getJobsCompletedPerDay() {
        return jobsCompletedPerDay;
    }

    public void setJobsCompletedPerDay(int jobsCompletedPerDay) {
        this.jobsCompletedPerDay = jobsCompletedPerDay;
    }

    public Map<Technician, Double> getTechnicianUtilization() {
        return technicianUtilization == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(technicianUtilization);
    }

    public void setTechnicianUtilization(Map<Technician, Double> technicianUtilization) {
        this.technicianUtilization = technicianUtilization;
    }

    public Map<Technician, Integer> getJobsCompletedByTechnician() {
        return jobsCompletedByTechnician == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(jobsCompletedByTechnician);
    }

    public void setJobsCompletedByTechnician(Map<Technician, Integer> jobsCompletedByTechnician) {
        this.jobsCompletedByTechnician = jobsCompletedByTechnician;
    }

    public Map<RepairBay, Double> getBayUtilization() {
        return bayUtilization == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(bayUtilization);
    }

    public void setBayUtilization(Map<RepairBay, Double> bayUtilization) {
        this.bayUtilization = bayUtilization;
    }

    public ValidationReport getValidationReport() {
        return validationReport;
    }

    public void setValidationReport(ValidationReport validationReport) {
        this.validationReport = validationReport;
    }
}
