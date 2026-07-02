package simulator.stochastic;

import java.util.Arrays;

public class PiecewiseConstantArrivalRate implements ArrivalRateFunction {
    private final double[] segmentStartsHours;
    private final double[] segmentRates;

    public PiecewiseConstantArrivalRate(double[] segmentStartsHours, double[] segmentRates) {
        if (segmentStartsHours.length == 0 || segmentStartsHours.length != segmentRates.length) {
            throw new IllegalArgumentException("Segment starts and rates must be non-empty and equal length");
        }

        this.segmentStartsHours = Arrays.copyOf(segmentStartsHours, segmentStartsHours.length);
        this.segmentRates = Arrays.copyOf(segmentRates, segmentRates.length);
    }

    public static PiecewiseConstantArrivalRate defaultDealershipDay() {
        return new PiecewiseConstantArrivalRate(
                new double[] {0.0, 1.0, 2.0, 4.0, 6.0, 8.0, 10.0},
                new double[] {8.0, 6.0, 4.0, 2.0, 3.0, 5.0, 3.0});
    }

    public PiecewiseConstantArrivalRate scaledToMeanRate(double targetMeanRate, double horizonHours) {
        if (horizonHours <= 0.0) {
            throw new IllegalArgumentException("horizonHours must be positive");
        }

        double currentMean = integratedRate(0.0, horizonHours) / horizonHours;
        if (currentMean <= 0.0) {
            return this;
        }

        double factor = targetMeanRate / currentMean;
        double[] scaledRates = new double[segmentRates.length];
        for (int i = 0; i < segmentRates.length; i++) {
            scaledRates[i] = segmentRates[i] * factor;
        }
        return new PiecewiseConstantArrivalRate(segmentStartsHours, scaledRates);
    }

    @Override
    public double rateAt(double timeHours) {
        int index = 0;
        for (int i = 0; i < segmentStartsHours.length; i++) {
            if (segmentStartsHours[i] <= timeHours) {
                index = i;
            } else {
                break;
            }
        }
        return segmentRates[index];
    }

    @Override
    public double integratedRate(double startTimeHours, double endTimeHours) {
        if (endTimeHours <= startTimeHours) {
            return 0.0;
        }

        double integrated = 0.0;
        for (int i = 0; i < segmentStartsHours.length; i++) {
            double segmentStart = segmentStartsHours[i];
            double segmentEnd = i + 1 < segmentStartsHours.length
                    ? segmentStartsHours[i + 1]
                    : Double.POSITIVE_INFINITY;

            double overlapStart = Math.max(startTimeHours, segmentStart);
            double overlapEnd = Math.min(endTimeHours, segmentEnd);
            if (overlapEnd > overlapStart) {
                integrated += segmentRates[i] * (overlapEnd - overlapStart);
            }
        }
        return integrated;
    }
}
