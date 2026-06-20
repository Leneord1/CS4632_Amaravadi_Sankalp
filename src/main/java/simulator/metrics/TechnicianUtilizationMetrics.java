package simulator.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import simulator.model.ServiceTicket;
import simulator.model.Technician;

public class TechnicianUtilizationMetrics {
    private final Map<Technician, Double> busyHoursByTechnician = new HashMap<>();
    private final Map<Technician, Double> technicianUtilization = new HashMap<>();
    private final Map<Technician, Integer> jobsCompletedByTechnician = new HashMap<>();
    private double simulationHorizonHours;

    public void setSimulationHorizonHours(double simulationHorizonHours) {
        this.simulationHorizonHours = simulationHorizonHours;
    }

    public void recordServiceTime(Technician technician, double serviceTimeHours) {
        if (technician == null || serviceTimeHours <= 0.0) {
            return;
        }

        double updatedBusyHours = busyHoursByTechnician.getOrDefault(technician, 0.0) + serviceTimeHours;
        busyHoursByTechnician.put(technician, updatedBusyHours);
        technicianUtilization.put(
                technician,
                MetricsEquations.resourceUtilization(updatedBusyHours, simulationHorizonHours));
    }

    public void recordCompletedJob(Technician technician, ServiceTicket ticket) {
        if (technician == null) {
            return;
        }

        jobsCompletedByTechnician.merge(technician, 1, Integer::sum);
        recordServiceTime(technician, ticket.getActualLaborTime());
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

    public double getShopTechnicianUtilization() {
        double totalBusyHours = busyHoursByTechnician.values().stream().mapToDouble(Double::doubleValue).sum();
        return MetricsEquations.shopResourceUtilization(
                totalBusyHours,
                busyHoursByTechnician.size(),
                simulationHorizonHours);
    }
}
