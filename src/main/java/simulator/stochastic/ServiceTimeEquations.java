package simulator.stochastic;

public final class ServiceTimeEquations {
    private ServiceTimeEquations() {
    }

    //  f(e_i) = 1 / (1 + alpha * e_i), e_i in [0, 1]
    public static double experienceScalingFactor(double experienceLevel, double alpha) {
        if (experienceLevel < 0.0 || experienceLevel > 1.0 || alpha < 0.0) {
            throw new IllegalArgumentException("experienceLevel must be in [0, 1] and alpha must be non-negative");
        }
        return 1.0 / (1.0 + alpha * experienceLevel);
    }

    // mu_i = mu_0 * f(e_i)
    public static double effectiveMeanServiceTime(double baseMeanServiceTime, double experienceLevel, double alpha) {
        if (baseMeanServiceTime < 0.0) {
            throw new IllegalArgumentException("baseMeanServiceTime must be non-negative");
        }
        return baseMeanServiceTime * experienceScalingFactor(experienceLevel, alpha);
    }

    //  theta_i = mu_i / k for T_i ~ Gamma(k, theta_i)
    public static double gammaScaleParameter(double meanServiceTime, double shapeParameter) {
        if (meanServiceTime < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException("meanServiceTime must be non-negative and shapeParameter must be positive");
        }
        return meanServiceTime / shapeParameter;
    }

    //  E[T_i] = mu_i
    public static double gammaMean(double scaleParameter, double shapeParameter) {
        if (scaleParameter < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException("scaleParameter must be non-negative and shapeParameter must be positive");
        }
        return scaleParameter * shapeParameter;
    }

    //  Var(T_i) = mu_i^2 / k
    public static double gammaVariance(double meanServiceTime, double shapeParameter) {
        if (meanServiceTime < 0.0 || shapeParameter <= 0.0) {
            throw new IllegalArgumentException("meanServiceTime must be non-negative and shapeParameter must be positive");
        }
        return (meanServiceTime * meanServiceTime) / shapeParameter;
    }

    public static double normalizeExperienceLevel(int experienceLevel, int maxExperienceLevel) {
        if (experienceLevel < 0 || maxExperienceLevel <= 0) {
            throw new IllegalArgumentException("experienceLevel and maxExperienceLevel must be positive");
        }
        return Math.min(1.0, (double) experienceLevel / maxExperienceLevel);
    }
}
