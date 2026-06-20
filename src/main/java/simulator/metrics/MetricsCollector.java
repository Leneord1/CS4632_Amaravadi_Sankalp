package simulator.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulator.model.Customer;
import simulator.model.ServiceTicket;
import simulator.model.Technician;

public class MetricsCollector {
    private static final Logger LOGGER = Logger.getLogger(MetricsCollector.class.getName());

    private double avgWaitTime;
    private final Map<Technician, Double> techUtilization = new HashMap<>();
    private int jobsCompletedPerDay;
    private final List<Double> partsDelays = new ArrayList<>();

    private final CustomerWaitMetrics customerWaitMetrics = new CustomerWaitMetrics();
    private final TechnicianUtilizationMetrics technicianUtilizationMetrics = new TechnicianUtilizationMetrics();
    private final PartsDelayMetrics partsDelayMetrics = new PartsDelayMetrics();
    private final ThroughputMetrics throughputMetrics = new ThroughputMetrics();
    private final QueueBenchmarkMetrics queueBenchmarkMetrics = new QueueBenchmarkMetrics();

    public void record(ServiceTicket ticket) {
    }

    public void recordCustomerCompletion(
            Customer customer,
            double advisorWaitTime,
            double serviceTime) {
    }

    public MetricsReport buildReport() {
        return new MetricsReport();
    }

    public void report() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Simulation metrics report is not implemented yet.");
        }
    }

    public double getAvgWaitTime() {
        return avgWaitTime;
    }

    public Map<Technician, Double> getTechUtilization() {
        return Map.copyOf(techUtilization);
    }

    public int getJobsCompletedPerDay() {
        return jobsCompletedPerDay;
    }

    public List<Double> getPartsDelays() {
        return List.copyOf(partsDelays);
    }

    public CustomerWaitMetrics getCustomerWaitMetrics() {
        return customerWaitMetrics;
    }

    public TechnicianUtilizationMetrics getTechnicianUtilizationMetrics() {
        return technicianUtilizationMetrics;
    }

    public PartsDelayMetrics getPartsDelayMetrics() {
        return partsDelayMetrics;
    }

    public ThroughputMetrics getThroughputMetrics() {
        return throughputMetrics;
    }

    public QueueBenchmarkMetrics getQueueBenchmarkMetrics() {
        return queueBenchmarkMetrics;
    }
}
