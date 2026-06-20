package simulator.metrics;

import java.util.Collections;
import java.util.Map;
import simulator.model.Technician;

public class MetricsReport {
    private double averageCustomerWaitTime;
    private double averagePartsDelay;
    private int jobsCompletedPerDay;
    private Map<Technician, Double> technicianUtilization;
    private Map<Technician, Integer> jobsCompletedByTechnician;

    public double getAverageCustomerWaitTime() {
        return averageCustomerWaitTime;
    }

    public void setAverageCustomerWaitTime(double averageCustomerWaitTime) {
        this.averageCustomerWaitTime = averageCustomerWaitTime;
    }

    public double getAveragePartsDelay() {
        return averagePartsDelay;
    }

    public void setAveragePartsDelay(double averagePartsDelay) {
        this.averagePartsDelay = averagePartsDelay;
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
}
