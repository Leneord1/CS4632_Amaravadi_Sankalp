package simulator.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import simulator.model.ServiceTicket;
import simulator.model.Technician;

public class TechnicianUtilizationMetrics {
    private final Map<Technician, Double> technicianUtilization = new HashMap<>();
    private final Map<Technician, Integer> jobsCompletedByTechnician = new HashMap<>();
    private double simulationHorizonHours;

    public void recordServiceTime(Technician technician, double serviceTimeHours) {
    }

    public void recordCompletedJob(Technician technician, ServiceTicket ticket) {
    }

    public void setSimulationHorizonHours(double simulationHorizonHours) {
        this.simulationHorizonHours = simulationHorizonHours;
    }

    public double getUtilization(Technician technician) {
        return technicianUtilization.getOrDefault(technician, 0.0);
    }

    public Map<Technician, Double> getTechnicianUtilization() {
        return Collections.unmodifiableMap(technicianUtilization);
    }

    public int getJobsCompletedByTechnician(Technician technician) {
        return jobsCompletedByTechnician.getOrDefault(technician, 0);
    }

    public Map<Technician, Integer> getJobsCompletedByTechnician() {
        return Collections.unmodifiableMap(jobsCompletedByTechnician);
    }

    public double getShopUtilization() {
        return 0.0;
    }
}
