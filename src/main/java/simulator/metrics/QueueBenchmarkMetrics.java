package simulator.metrics;

public class QueueBenchmarkMetrics {
    public double calculateSystemUtilization(
            double arrivalRate, int serverCount, double serviceRatePerServer) {
        return MetricsEquations.systemUtilization(arrivalRate, serverCount, serviceRatePerServer);
    }

    public double calculateProbabilityOfWaiting(
            double arrivalRate, int serverCount, double serviceRatePerServer) {
        return MetricsEquations.erlangCProbabilityOfWaiting(
                arrivalRate, serverCount, serviceRatePerServer);
    }

    public double calculateExpectedQueueWait(
            double arrivalRate, int serverCount, double serviceRatePerServer) {
        return MetricsEquations.expectedQueueWait(arrivalRate, serverCount, serviceRatePerServer);
    }
}
