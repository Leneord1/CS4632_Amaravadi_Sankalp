package simulator.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulator.model.Customer;
import simulator.model.RepairBay;
import simulator.model.ServiceTicket;
import simulator.model.Technician;
import simulator.model.TicketStatus;
import simulator.config.SimulationConfig;

public class MetricsCollector {
    private static final Logger LOGGER = Logger.getLogger(MetricsCollector.class.getName());

    private double avgWaitTime;
    private final List<Double> partsDelays = new ArrayList<>();
    private int jobsCompletedPerDay;
    private double simulationHorizonHours;
    private double arrivalRate;
    private int technicianCount;
    private double serviceRatePerTechnician;
    private double validationRelativeTolerance;

    private final CustomerWaitMetrics customerWaitMetrics = new CustomerWaitMetrics();
    private final TechnicianUtilizationMetrics technicianUtilizationMetrics = new TechnicianUtilizationMetrics();
    private final BayUtilizationMetrics bayUtilizationMetrics = new BayUtilizationMetrics();
    private final PartsDelayMetrics partsDelayMetrics = new PartsDelayMetrics();
    private final ThroughputMetrics throughputMetrics = new ThroughputMetrics();
    private final QueueBenchmarkMetrics queueBenchmarkMetrics = new QueueBenchmarkMetrics();
    private final SimulationValidationMetrics simulationValidationMetrics = new SimulationValidationMetrics();

    public void configureSimulation(SimulationConfig config) {
        this.simulationHorizonHours = config.getSimulationHorizonHours();
        this.arrivalRate = config.getArrivalRate();
        this.technicianCount = config.getTechnicianCount();
        this.serviceRatePerTechnician = config.getServiceRatePerTechnician();
        this.validationRelativeTolerance = config.getValidationRelativeTolerance();
        technicianUtilizationMetrics.setSimulationHorizonHours(simulationHorizonHours);
        bayUtilizationMetrics.setSimulationHorizonHours(simulationHorizonHours);
    }

    public void configureSimulation(
            double simulationHorizonHours,
            double arrivalRate,
            int technicianCount,
            double serviceRatePerTechnician) {
        configureSimulation(
                SimulationConfig.builder()
                        .simulationHorizonHours(simulationHorizonHours)
                        .arrivalRate(arrivalRate)
                        .technicianCount(technicianCount)
                        .serviceRatePerTechnician(serviceRatePerTechnician)
                        .build());
    }

    public void recordTicket(ServiceTicket ticket) {
        partsDelayMetrics.recordPartsDelay(ticket);
        partsDelayMetrics.recordTotalJobDelay(ticket);
        partsDelays.add(ticket.getPartsDelay());

        if (ticket.getStatus() == TicketStatus.COMPLETE) {
            throughputMetrics.recordJobCompletion(ticket);
            jobsCompletedPerDay = throughputMetrics.getJobsCompletedPerDay();
            Technician technician = ticket.getAssignedTechnician();
            if (technician != null) {
                technicianUtilizationMetrics.recordCompletedJob(technician, ticket);
                RepairBay bay = technician.getAssignedBay();
                if (bay != null) {
                    bayUtilizationMetrics.recordOccupiedTime(bay, ticket.getActualLaborTime());
                }
            }
        }
    }

    public void recordCustomerCompletion(
            Customer customer,
            double advisorWaitTime,
            double serviceTime) {
        customerWaitMetrics.recordCompletedCustomer(customer, advisorWaitTime, serviceTime);
        avgWaitTime = customerWaitMetrics.getAverageWaitTime();
    }

    public void recordTechnicianServiceTime(Technician technician, double serviceTimeHours) {
        technicianUtilizationMetrics.recordServiceTime(technician, serviceTimeHours);
    }

    public void recordBayOccupiedTime(RepairBay bay, double occupiedHours) {
        bayUtilizationMetrics.recordOccupiedTime(bay, occupiedHours);
    }

    public void printSnapshot(String label) {
        System.out.printf(
                "[Metrics] %s jobsCompleted=%d avgCustomerWait=%.3fh%n",
                label, throughputMetrics.getRecordedJobs(), customerWaitMetrics.getAverageWaitTime());
    }

    public MetricsReport buildReport() {
        MetricsReport report = new MetricsReport();
        report.setAverageCustomerWaitTime(customerWaitMetrics.getAverageWaitTime());
        report.setAverageAdvisorWaitTime(customerWaitMetrics.getAverageAdvisorWaitTime());
        report.setAverageQueueDelay(customerWaitMetrics.getAverageQueueDelay());
        report.setAveragePartsDelay(customerWaitMetrics.getAveragePartsDelay());
        report.setAverageServiceTime(customerWaitMetrics.getAverageServiceTime());
        report.setAverageTotalJobDelay(customerWaitMetrics.getAverageTotalJobDelay());
        report.setJobsCompletedPerDay(throughputMetrics.getJobsCompletedPerDay());
        report.setTechnicianUtilization(technicianUtilizationMetrics.getTechnicianUtilization());
        report.setJobsCompletedByTechnician(technicianUtilizationMetrics.getJobsCompletedByTechnician());
        report.setBayUtilization(bayUtilizationMetrics.getBayUtilizationByBay());
        report.setSimulatedShopTechnicianUtilization(technicianUtilizationMetrics.getShopTechnicianUtilization());
        report.setSimulatedShopBayUtilization(bayUtilizationMetrics.getShopBayUtilization());
        report.setAnalyticalSystemUtilization(
                queueBenchmarkMetrics.calculateSystemUtilization(
                        arrivalRate,
                        technicianCount,
                        serviceRatePerTechnician));
        report.setAnalyticalQueueWait(
                queueBenchmarkMetrics.calculateExpectedQueueWait(
                        arrivalRate,
                        technicianCount,
                        serviceRatePerTechnician));
        report.setValidationReport(
                simulationValidationMetrics.validateAgainstQueueBenchmark(
                        report,
                        arrivalRate,
                        technicianCount,
                        serviceRatePerTechnician,
                        customerWaitMetrics.getAverageAdvisorWaitTime(),
                        customerWaitMetrics.getAverageServiceTime(),
                        validationRelativeTolerance));
        return report;
    }

    public void report() {
        MetricsReport metricsReport = buildReport();
        if (!LOGGER.isLoggable(Level.INFO)) {
            return;
        }

        LOGGER.info(String.format("Average customer wait (W_total): %.3f h", metricsReport.getAverageCustomerWaitTime()));
        LOGGER.info(String.format(
                "Wait components W_advisor=%.3f D_queue=%.3f D_parts=%.3f T_service=%.3f",
                metricsReport.getAverageAdvisorWaitTime(),
                metricsReport.getAverageQueueDelay(),
                metricsReport.getAveragePartsDelay(),
                metricsReport.getAverageServiceTime()));
        LOGGER.info(String.format(
                "Shop utilization simulated tech=%.3f bay=%.3f analytical rho=%.3f",
                metricsReport.getSimulatedShopTechnicianUtilization(),
                metricsReport.getSimulatedShopBayUtilization(),
                metricsReport.getAnalyticalSystemUtilization()));
        LOGGER.info(String.format("Analytical queue wait Wq=%.3f h", metricsReport.getAnalyticalQueueWait()));
        ValidationReport validationReport = metricsReport.getValidationReport();
        if (validationReport != null) {
            LOGGER.info(String.format(
                    "Validation rho error=%.3f Wq error=%.3f overall=%s",
                    validationReport.getUtilizationRelativeError(),
                    validationReport.getQueueWaitRelativeError(),
                    validationReport.isOverallValid() ? "PASS" : "PENDING"));
        }
    }

    public double getAvgWaitTime() {
        return avgWaitTime;
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

    public BayUtilizationMetrics getBayUtilizationMetrics() {
        return bayUtilizationMetrics;
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

    public SimulationValidationMetrics getSimulationValidationMetrics() {
        return simulationValidationMetrics;
    }

    public double getSimulationHorizonHours() {
        return simulationHorizonHours;
    }

    public void setSimulationHorizonHours(double simulationHorizonHours) {
        this.simulationHorizonHours = simulationHorizonHours;
    }

    public SimulationConfig getSimulationConfig() {
        return SimulationConfig.builder()
                .simulationHorizonHours(simulationHorizonHours)
                .arrivalRate(arrivalRate)
                .technicianCount(technicianCount)
                .serviceRatePerTechnician(serviceRatePerTechnician)
                .validationRelativeTolerance(validationRelativeTolerance)
                .build();
    }
}
