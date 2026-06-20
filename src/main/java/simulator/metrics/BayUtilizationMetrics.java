package simulator.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import simulator.model.RepairBay;

public class BayUtilizationMetrics {
    private final Map<RepairBay, Double> busyHoursByBay = new HashMap<>();
    private final Map<RepairBay, Double> bayUtilization = new HashMap<>();
    private double simulationHorizonHours;

    public void setSimulationHorizonHours(double simulationHorizonHours) {
        this.simulationHorizonHours = simulationHorizonHours;
    }

    public void recordOccupiedTime(RepairBay bay, double occupiedHours) {
        if (bay == null || occupiedHours <= 0.0) {
            return;
        }

        double updatedBusyHours = busyHoursByBay.getOrDefault(bay, 0.0) + occupiedHours;
        busyHoursByBay.put(bay, updatedBusyHours);
        bayUtilization.put(bay, MetricsEquations.resourceUtilization(updatedBusyHours, simulationHorizonHours));
    }

    public double getBayUtilization(RepairBay bay) {
        return bayUtilization.getOrDefault(bay, 0.0);
    }

    public Map<RepairBay, Double> getBayUtilizationByBay() {
        return Collections.unmodifiableMap(bayUtilization);
    }

    public double getShopBayUtilization() {
        double totalBusyHours = busyHoursByBay.values().stream().mapToDouble(Double::doubleValue).sum();
        return MetricsEquations.shopResourceUtilization(
                totalBusyHours,
                busyHoursByBay.size(),
                simulationHorizonHours);
    }
}
