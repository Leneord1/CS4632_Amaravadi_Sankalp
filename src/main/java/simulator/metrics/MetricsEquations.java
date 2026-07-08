package simulator.metrics;

public final class MetricsEquations {
    private MetricsEquations() {
    }

    public static double systemUtilization(double arrivalRate, int serverCount, double serviceRatePerServer) {
        // Equation: ρ = λ / (c * μ)
        if (serverCount <= 0 || serviceRatePerServer <= 0.0) {
            return 0.0;
        }
        return arrivalRate / (serverCount * serviceRatePerServer);
    }

    public static double erlangCProbabilityOfWaiting(
            // Equation: P_w = (cρ)^c / (c! * (1 - ρ)) * (1 / (1 - ρ))
            double arrivalRate,
            int serverCount,
            double serviceRatePerServer) {
        if (serverCount <= 0 || serviceRatePerServer <= 0.0) {
            return 0.0;
        }

        double offeredLoad = arrivalRate / serviceRatePerServer;
        double utilization = systemUtilization(arrivalRate, serverCount, serviceRatePerServer);
        if (utilization >= 1.0) {
            return 1.0;
        }

        double prefixSum = 0.0;
        for (int serversInSystem = 0; serversInSystem < serverCount; serversInSystem++) {
            prefixSum += Math.pow(offeredLoad, serversInSystem) / factorial(serversInSystem);
        }

        double erlangDenominatorTerm = Math.pow(offeredLoad, serverCount) / factorial(serverCount);
        double waitingTerm = erlangDenominatorTerm * (serverCount / (serverCount - offeredLoad));
        return waitingTerm / (prefixSum + waitingTerm);
    }

    public static double expectedQueueWait(double arrivalRate, int serverCount, double serviceRatePerServer) {
        /* Equation: E[W] = P_w / (cμ - λ) */
        double probabilityOfWaiting = erlangCProbabilityOfWaiting(arrivalRate, serverCount, serviceRatePerServer);
        double remainingCapacity = (serverCount * serviceRatePerServer) - arrivalRate;
        if (remainingCapacity <= 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        return probabilityOfWaiting / remainingCapacity;
    }

    public static double relativeError(double observed, double expected) {
        // Equation: RE = |O - E| / |E|
        if (expected == 0.0) {
            return observed == 0.0 ? 0.0 : Double.POSITIVE_INFINITY;
        }
        return Math.abs(observed - expected) / Math.abs(expected);
    }

    public static boolean withinRelativeTolerance(double observed, double expected, double relativeTolerance) {
        // Equation: |RE| <= RT
        if (relativeTolerance < 0.0) {
            throw new IllegalArgumentException("relativeTolerance must be non-negative");
        }
        return relativeError(observed, expected) <= relativeTolerance;
    }

    public static double totalCustomerWaitTime(
            // Equation: T_C = W_A + D_Q + D_P + S
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
        // Equation: ρ = busyHours / horizonHours
        if (horizonHours <= 0.0) {
            return 0.0;
        }
        return busyHours / horizonHours;
    }

    public static double shopResourceUtilization(double totalBusyHours, int resourceCount, double horizonHours) {
        // Equation: ρ = totalBusyHours / (resourceCount * horizonHours)
        if (resourceCount <= 0 || horizonHours <= 0.0) {
            return 0.0;
        }
        return totalBusyHours / (resourceCount * horizonHours);
    }

    private static double factorial(int n) {
        /*
         Equation: n! = n * (n-1) * ... * 2 * 1
         Compute n! iteratively to avoid stack overflow for large n
        */
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        double product = 1.0;
        for (int i = 2; i <= n; i++) {
            product *= i;
        }
        return product;
    }
}
