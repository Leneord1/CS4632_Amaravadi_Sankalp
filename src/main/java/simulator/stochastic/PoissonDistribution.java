package simulator.stochastic;

public final class PoissonDistribution {
    private PoissonDistribution() {
    }

    public static double probabilityExactlyNArrivals(int n, double lambda, double timeHours) {
        if (n < 0 || lambda < 0.0 || timeHours < 0.0) {
            throw new IllegalArgumentException("n, lambda, and time must be non-negative");
        }

        double lambdaT = lambda * timeHours;
        return Math.pow(lambdaT, n) * Math.exp(-lambdaT) / factorial(n);
    }

    public static double probabilityExactlyNArrivals(int n, double integratedIntensity) {
        if (n < 0 || integratedIntensity < 0.0) {
            throw new IllegalArgumentException("n and integrated intensity must be non-negative");
        }

        return Math.pow(integratedIntensity, n)
                * Math.exp(-integratedIntensity)
                / factorial(n);
    }

    private static double factorial(int n) {
        double product = 1.0;
        for (int i = 2; i <= n; i++) {
            product *= i;
        }
        return product;
    }
    private static double sampleExponentialInterArrival(double lambda) {
        if (lambda <= 0.0) {
            throw new IllegalArgumentException("Lambda must be positive");
        }
        return -Math.log(1.0 - Math.random()) / lambda;
    }

    private static double samplePoisson(double lambda) {
        if (lambda < 0.0) {
            throw new IllegalArgumentException("Lambda must be non-negative");
        }
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= Math.random();
        } while (p > L);
        return k - 1;
    }
}
