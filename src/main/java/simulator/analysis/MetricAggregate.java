package simulator.analysis;

/**
 * Mean, sample std-dev, min, max, and 95% CI for one metric across replications.
 */
public final class MetricAggregate {
    private final String name;
    private final int n;
    private final double mean;
    private final double stdDev;
    private final double min;
    private final double max;
    private final double ciHalfWidth;

    private MetricAggregate(
            String name, int n, double mean, double stdDev, double min, double max, double ciHalfWidth) {
        this.name = name;
        this.n = n;
        this.mean = mean;
        this.stdDev = stdDev;
        this.min = min;
        this.max = max;
        this.ciHalfWidth = ciHalfWidth;
    }

    public static MetricAggregate of(String name, double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("values must be non-empty");
        }
        int n = values.length;
        double sum = 0.0;
        double min = values[0];
        double max = values[0];
        for (double v : values) {
            sum += v;
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        double mean = sum / n;
        double variance = 0.0;
        if (n > 1) {
            for (double v : values) {
                double d = v - mean;
                variance += d * d;
            }
            variance /= (n - 1.0);
        }
        double stdDev = Math.sqrt(variance);
        double ciHalfWidth = n > 0 ? 1.96 * (stdDev / Math.sqrt(n)) : 0.0;
        return new MetricAggregate(name, n, mean, stdDev, min, max, ciHalfWidth);
    }

    public String getName() {
        return name;
    }

    public int getN() {
        return n;
    }

    public double getMean() {
        return mean;
    }

    public double getStdDev() {
        return stdDev;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getCiHalfWidth() {
        return ciHalfWidth;
    }

    public double getCiLow() {
        return mean - ciHalfWidth;
    }

    public double getCiHigh() {
        return mean + ciHalfWidth;
    }

    /**
     * Sensitivity = (%Δ output) / (%Δ input) relative to a baseline.
     */
    public static double sensitivity(double baselineInput, double newInput, double baselineOut, double newOut) {
        if (baselineInput == 0.0 || baselineOut == 0.0) {
            return Double.NaN;
        }
        double pctIn = (newInput - baselineInput) / baselineInput;
        double pctOut = (newOut - baselineOut) / baselineOut;
        if (pctIn == 0.0) {
            return Double.NaN;
        }
        return pctOut / pctIn;
    }

    public String toCsvRow() {
        return String.format(
                java.util.Locale.US,
                "%s,%d,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f",
                name,
                n,
                mean,
                stdDev,
                min,
                max,
                getCiLow(),
                getCiHigh());
    }

    public static String csvHeader() {
        return "metric,n,mean,std_dev,min,max,ci95_low,ci95_high";
    }
}
