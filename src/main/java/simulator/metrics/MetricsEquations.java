package simulator.metrics;

public final class MetricsEquations {
    private MetricsEquations() {
    }

    public static double systemUtilization(double arrivalRate, int serverCount, double serviceRatePerServer) {
        if (serverCount <= 0 || serviceRatePerServer <= 0.0) {
            return 0.0;
        }
        return arrivalRate / (serverCount * serviceRatePerServer);
    }

    public static double totalCustomerWaitTime(
            double advisorWaitTime,
            double queueDelay,
            double partsDelay,
            double serviceTime) {
        return advisorWaitTime + queueDelay + partsDelay + serviceTime;
    }

    public static double totalJobDelay(double queueDelay, double partsDelay) {
        return queueDelay + partsDelay;
    }

    public static double resourceUtilization(double busyHours, double horizonHours) {
        if (horizonHours <= 0.0) {
            return 0.0;
        }
        return busyHours / horizonHours;
    }

    public static double shopResourceUtilization(double totalBusyHours, int resourceCount, double horizonHours) {
        if (resourceCount <= 0 || horizonHours <= 0.0) {
            return 0.0;
        }
        return totalBusyHours / (resourceCount * horizonHours);
    }
}
