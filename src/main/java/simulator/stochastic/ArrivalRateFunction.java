package simulator.stochastic;

@FunctionalInterface
public interface ArrivalRateFunction {
    double rateAt(double timeHours);

    default double integratedRate(double startTimeHours, double endTimeHours) {
        /*
           Use the trapezoidal rule to integrate
           the arrival rate function over the given
           time interval.
        */
        if (endTimeHours <= startTimeHours) {
            return 0.0;
        }

        double integrated = 0.0;
        double step = Math.min(0.01, (endTimeHours - startTimeHours) / 1000.0);
        double time = startTimeHours;
        while (time < endTimeHours) {
            double nextTime = Math.min(time + step, endTimeHours);
            integrated += 0.5 * (rateAt(time) + rateAt(nextTime)) * (nextTime - time);
            time = nextTime;
        }
        return integrated;
    }

    default double maxRate(double startTimeHours, double endTimeHours) {
        //  Use a simple sampling method to find the maximum arrival
        //  rate over the given time interval.
        double maxRate = 0.0;
        double step = Math.min(0.05, (endTimeHours - startTimeHours) / 200.0);
        if (step <= 0.0) {
            return rateAt(startTimeHours);
        }

        for (double time = startTimeHours; time <= endTimeHours; time += step) {
            maxRate = Math.max(maxRate, rateAt(time));
        }
        return maxRate;
    }
}
