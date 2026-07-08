package simulator.stochastic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simulator.model.Customer;

import java.util.List;
import java.util.Random;

class CoxProcessTest {
    @Test
    void arrivalRateScalesWithMultiplier() {
        CoxProcess process = new CoxProcess(time -> 5.0, new Random(1), 2.0);
        assertEquals(10.0, process.arrivalRateAt(1.0));
    }

    @Test
    void integratedIntensityUsesRateFunction() {
        CoxProcess process = new CoxProcess(time -> 4.0, new Random(1), 1.0);
        assertEquals(8.0, process.integratedIntensity(1.0, 3.0), 1e-9);
    }

    @Test
    void probabilityDelegatesToIntegratedIntensity() {
        CoxProcess process = new CoxProcess(time -> 2.0, new Random(1), 1.0);
        double expected = PoissonDistribution.probabilityExactlyNArrivals(0, 4.0);
        assertEquals(expected, process.probabilityExactlyNArrivals(0, 0.0, 2.0), 1e-9);
    }

    @Test
    void generateArrivalsReturnsCustomersWithinHorizon() {
        CoxProcess process = new CoxProcess(time -> 6.0, new Random(42), 1.0);
        List<Customer> arrivals = process.generateArrivals(0.0, 5.0, 20);

        assertFalse(arrivals.isEmpty());
        for (Customer customer : arrivals) {
            assertTrue(customer.getArrivalTime() < 5.0);
        }
    }

    @Test
    void generateArrivalsRespectsMaxArrivalCap() {
        CoxProcess process = new CoxProcess(time -> 100.0, new Random(7), 1.0);
        List<Customer> arrivals = process.generateArrivals(0.0, 10.0, 3);
        assertEquals(3, arrivals.size());
    }

    @Test
    void sampleNextArrivalReturnsNegativeWhenHorizonAlreadyPassed() {
        CoxProcess process = CoxProcess.defaultDealershipProcess();
        assertEquals(-1.0, process.sampleNextArrivalTime(10.0, 10.0));
    }

    @Test
    void sampleNextArrivalReturnsNegativeForZeroRate() {
        CoxProcess process = new CoxProcess(time -> 0.0, new Random(1), 1.0);
        assertEquals(-1.0, process.sampleNextArrivalTime(0.0, 5.0));
    }

    @Test
    void stationaryProbabilityUsesEquationFive() {
        CoxProcess process = new CoxProcess(time -> 4.0, new Random(1), 1.0);
        double expected = PoissonDistribution.probabilityExactlyNArrivals(1, 4.0, 0.5);
        assertEquals(expected, process.probabilityExactlyNArrivalsStationary(1, 4.0, 0.5), 1e-9);
    }

    @Test
    void sampleArrivalCountUsesSeededRandom() {
        CoxProcess process = new CoxProcess(time -> 3.0, new Random(99), 1.0);
        assertEquals(9, process.sampleArrivalCount(0.0, 2.0));
    }

    @Test
    void generateArrivalsReturnsEmptyListWhenHorizonIsImmediate() {
        CoxProcess process = CoxProcess.defaultDealershipProcess();
        List<Customer> arrivals = process.generateArrivals(2.0, 2.0, 10);
        assertTrue(arrivals.isEmpty());
    }
}
