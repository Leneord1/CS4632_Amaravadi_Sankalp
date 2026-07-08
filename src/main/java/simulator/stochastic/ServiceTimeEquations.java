package simulator.stochastic;

public final class ServiceTimeEquations {
    private ServiceTimeEquations() {}

    public static double experienceScalingFactor(double experienceLevel, double alpha) {
        //  f(e_i) = 1 / (1 + alpha * e_i), e_i in [0, 1]
        if (experienceLevel < 0.0 || experienceLevel > 1.0 || alpha < 0.0) {
            throw new IllegalArgumentException(
                    "experienceLevel must be in [0, 1] and alpha must be non-negative");
        }
        return 1.0 / (1.0 + alpha * experienceLevel);
    }

    public static double effectiveMeanServiceTime(
            double baseMeanServiceTime, double experienceLevel, double alpha) {
        // mu_i = mu_0 * f(e_i)
        if (baseMeanServiceTime < 0.0) {
            throw new IllegalArgumentException("baseMeanServiceTime must be non-negative");
        }
        return baseMeanServiceTime * experienceScalingFactor(experienceLevel, alpha);
    }

    public static double gammaScaleParameter(double meanServiceTime, double shapeParameter) {
        //  theta_i = mu_i / k for T_i ~ Gamma(k, theta_i)
        if (meanServiceTime < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException(
                    "meanServiceTime must be non-negative and shapeParameter must be positive");
        }
        return meanServiceTime / shapeParameter;
    }

    public static double gammaMean(double scaleParameter, double shapeParameter) {
        //  E[T_i] = mu_i = k * theta_i for T_i ~ Gamma(k, theta_i)
        if (scaleParameter < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException(
                    "scaleParameter must be non-negative and shapeParameter must be positive");
        }
        return scaleParameter * shapeParameter;
    }

    public static double gammaVariance(double meanServiceTime, double shapeParameter) {
        //  Var(T_i) = mu_i^2 / k
        if (meanServiceTime < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException(
                    "meanServiceTime must be non-negative and shapeParameter must be positive");
        }
        return (meanServiceTime * meanServiceTime) / shapeParameter;
    }

    public static double normalizeExperienceLevel(int experienceLevel, int maxExperienceLevel) {
        //  Normalize the experience level to [0, 1]
        //  e_i = min(1, e_i / E_max)
        if (experienceLevel < 0 || maxExperienceLevel <= 0) {
            throw new IllegalArgumentException(
                    "experienceLevel and maxExperienceLevel must be positive");
        }
        return Math.min(1.0, (double) experienceLevel / maxExperienceLevel);
    }
}
