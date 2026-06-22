package simulator.stochastic;

import java.util.Random;

public final class GammaDistribution {
    private GammaDistribution() {
    }

    // Samples T_i ~ Gamma(k, theta_i) for service times.
    public static double sample(Random random, double shapeParameter, double scaleParameter) {
        if (shapeParameter <= 0.0 || scaleParameter <= 0.0) {
            throw new IllegalArgumentException("shapeParameter and scaleParameter must be positive");
        }

        double gammaSample = 0.0;
        int wholeShape = (int) Math.floor(shapeParameter);
        double fractionalShape = shapeParameter - wholeShape;

        for (int i = 0; i < wholeShape; i++) {
            gammaSample += sampleExponential(random, 1.0);
        }

        if (fractionalShape > 0.0) {
            gammaSample += sampleExponential(random, 1.0 / fractionalShape);
        }

        return gammaSample * scaleParameter;
    }

    public static double sampleForTechnician(
            Random random,
            double baseMeanServiceTime,
            double experienceLevel,
            double alpha,
            double shapeParameter) {
        double meanServiceTime = ServiceTimeEquations.effectiveMeanServiceTime(
                baseMeanServiceTime,
                experienceLevel,
                alpha);
        double scaleParameter = ServiceTimeEquations.gammaScaleParameter(meanServiceTime, shapeParameter);
        return sample(random, shapeParameter, scaleParameter);
    }

    private static double sampleExponential(Random random, double rate) {
        return -Math.log(1.0 - random.nextDouble()) / rate;
    }
}
