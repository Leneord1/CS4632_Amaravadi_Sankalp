package simulator.stochastic;

import java.util.Random;

public final class PoissonDistribution {
    private PoissonDistribution() {}

    public static double probabilityExactlyNArrivals(int n, double lambda, double timeHours) {
        //  Validate inputs

        if (n < 0 || lambda < 0.0 || timeHours < 0.0) {
            throw new IllegalArgumentException("n, lambda, and time must be non-negative");
        }

        double lambdaT = lambda * timeHours;
        return Math.pow(lambdaT, n) * Math.exp(-lambdaT) / factorial(n);
    }

    public static double probabilityExactlyNArrivals(int n, double integratedIntensity) {
        //  Validate inputs
        if (n < 0 || integratedIntensity < 0.0) {
            throw new IllegalArgumentException("n and integrated intensity must be non-negative");
        }

        return Math.pow(integratedIntensity, n) * Math.exp(-integratedIntensity) / factorial(n);
    }

    public static double sampleExponentialInterArrival(Random random, double lambda) {
        //  Validate inputs
        if (lambda <= 0.0) {
            throw new IllegalArgumentException("Lambda must be positive");
        }
        return -Math.log(1.0 - random.nextDouble()) / lambda;
    }

    public static int samplePoisson(Random random, double lambda) {
        //  Validate inputs
        if (lambda < 0.0) {
            throw new IllegalArgumentException("Lambda must be non-negative");
        }
        double threshold = Math.exp(-lambda);
        int count = 0;
        double product = 1.0;
        do {
            count++;
            product *= random.nextDouble();
        } while (product > threshold);
        return count - 1;
    }

    private static double factorial(int n) {
        double product = 1.0;
        for (int i = 2; i <= n; i++) {
            product *= i;
        }
        return product;
    }
}
