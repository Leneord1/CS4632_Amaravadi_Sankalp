package simulator.metrics;

public class QueueBenchmarkMetrics {
    public double calculateSystemUtilization(double arrivalRate, int serverCount, double serviceRatePerServer) {
        return MetricsEquations.systemUtilization(arrivalRate, serverCount, serviceRatePerServer);
    }

    public double calculateProbabilityOfWaiting(double arrivalRate, int serverCount, double serviceRatePerServer) {
        if (serverCount <= 0 || serviceRatePerServer <= 0.0) {
            return 0.0;
        }

        double offeredLoad = arrivalRate / serviceRatePerServer;
        double utilization = calculateSystemUtilization(arrivalRate, serverCount, serviceRatePerServer);
        if (utilization >= 1.0) {
            return 1.0;
        }

        double prefixSum = 0.0;
        for (int n = 0; n < serverCount; n++) {
            prefixSum += Math.pow(offeredLoad, n) / factorial(n);
        }

        double erlangDenominatorTerm = Math.pow(offeredLoad, serverCount) / factorial(serverCount);
        double waitingTerm = erlangDenominatorTerm * (serverCount / (serverCount - offeredLoad));
        return waitingTerm / (prefixSum + waitingTerm);
    }

    public double calculateExpectedQueueWait(double arrivalRate, int serverCount, double serviceRatePerServer) {
        double probabilityOfWaiting = calculateProbabilityOfWaiting(arrivalRate, serverCount, serviceRatePerServer);
        double remainingCapacity = (serverCount * serviceRatePerServer) - arrivalRate;
        if (remainingCapacity <= 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        return probabilityOfWaiting / remainingCapacity;
    }

    private static double factorial(int n) {
        double product = 1.0;
        for (int i = 2; i <= n; i++) {
            product *= i;
        }
        return product;
    }
}
