package simulator.stochastic;

import simulator.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoxProcess {
    private static final Logger LOGGER = Logger.getLogger(CoxProcess.class.getName());

    private final ArrivalRateFunction rateFunction;
    private final Random random;
    private final double stochasticIntensityMultiplier;

    public CoxProcess(ArrivalRateFunction rateFunction) {
        this(rateFunction, new Random(), 1.0);
    }

    public CoxProcess(
            ArrivalRateFunction rateFunction, Random random, double stochasticIntensityMultiplier) {
        this.rateFunction = rateFunction;
        this.random = random;
        this.stochasticIntensityMultiplier = stochasticIntensityMultiplier;
    }

    public static CoxProcess defaultDealershipProcess() {
        return new CoxProcess(PiecewiseConstantArrivalRate.defaultDealershipDay());
    }

    public void printRate(double timeHours) {
        LOGGER.info(
                String.format(
                        "[CoxProcess] lambda(t=%.2fh)=%.2f vehicles/h",
                        timeHours, arrivalRateAt(timeHours)));
    }

    public double arrivalRateAt(double timeHours) {
        return rateFunction.rateAt(timeHours) * stochasticIntensityMultiplier;
    }

    public double integratedIntensity(double startTimeHours, double endTimeHours) {
        return rateFunction.integratedRate(startTimeHours, endTimeHours)
                * stochasticIntensityMultiplier;
    }

    public double probabilityExactlyNArrivals(int n, double startTimeHours, double endTimeHours) {
        double integratedLambda = integratedIntensity(startTimeHours, endTimeHours);
        return PoissonDistribution.probabilityExactlyNArrivals(n, integratedLambda);
    }

    public double probabilityExactlyNArrivalsStationary(int n, double lambda, double timeHours) {
        return PoissonDistribution.probabilityExactlyNArrivals(n, lambda, timeHours);
    }

    public double sampleNextArrivalTime(double afterTimeHours, double horizonEndHours) {
        /*
           Use thinning method to sample the next arrival time
           from a non-homogeneous Poisson process.
        */
        if (afterTimeHours >= horizonEndHours) {
            return -1.0;
        }

        double lambdaMax =
                rateFunction.maxRate(afterTimeHours, horizonEndHours)
                        * stochasticIntensityMultiplier;
        if (lambdaMax <= 0.0) {
            return -1.0;
        }

        double candidateTime = afterTimeHours;
        while (true) {
            candidateTime += PoissonDistribution.sampleExponentialInterArrival(random, lambdaMax);
            if (candidateTime >= horizonEndHours) {
                return -1.0;
            }

            double acceptanceProbability = arrivalRateAt(candidateTime) / lambdaMax;
            if (random.nextDouble() < acceptanceProbability) {
                return candidateTime;
            }
        }
    }

    public List<Customer> generateArrivals(
            double startTimeHours, double endTimeHours, int maxArrivals) {
        /*
           Generate a list of customer arrivals using the thinning method
           for a non-homogeneous Poisson process.
        */
        List<Customer> arrivals = new ArrayList<>();
        double nextTime = startTimeHours;

        while (arrivals.size() < maxArrivals) {
            nextTime = sampleNextArrivalTime(nextTime, endTimeHours);
            if (nextTime < 0.0) {
                break;
            }

            Customer customer = new Customer(nextTime);
            arrivals.add(customer);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine(
                        String.format(
                                "Cox arrival at t=%.3f h (lambda=%.3f vehicles/h)",
                                nextTime, arrivalRateAt(nextTime)));
            }
        }

        return arrivals;
    }

    public int sampleArrivalCount(double startTimeHours, double endTimeHours) {
        return PoissonDistribution.samplePoisson(
                random, integratedIntensity(startTimeHours, endTimeHours));
    }
}
