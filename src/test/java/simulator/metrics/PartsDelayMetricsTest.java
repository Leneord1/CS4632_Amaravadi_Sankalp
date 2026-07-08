package simulator.metrics;

import org.junit.jupiter.api.Test;
import simulator.model.ServiceTicket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartsDelayMetricsTest {

    private static ServiceTicket ticket(double queueDelay, double partsDelay) {
        ServiceTicket ticket = new ServiceTicket(1, "Oil Change", 0.3, 0.7);
        ticket.setQueueDelay(queueDelay);
        ticket.setPartsDelay(partsDelay);
        return ticket;
    }

    @Test
    void emptyMetricsReturnZeroAverages() {
        PartsDelayMetrics metrics = new PartsDelayMetrics();
        assertEquals(0.0, metrics.getAveragePartsDelay(), 1e-9);
        assertEquals(0.0, metrics.getAverageTotalJobDelay(), 1e-9);
        assertEquals(0.0, metrics.getTotalPartsDelay(), 1e-9);
        assertTrue(metrics.getPartsDelays().isEmpty());
    }

    @Test
    void averagesReflectRecordedTickets() {
        PartsDelayMetrics metrics = new PartsDelayMetrics();
        ServiceTicket first = ticket(0.5, 1.0);
        ServiceTicket second = ticket(1.5, 3.0);

        metrics.recordPartsDelay(first);
        metrics.recordTotalJobDelay(first);
        metrics.recordPartsDelay(second);
        metrics.recordTotalJobDelay(second);

        assertEquals(4.0, metrics.getTotalPartsDelay(), 1e-9);
        assertEquals(2.0, metrics.getAveragePartsDelay(), 1e-9);

        double expectedTotalJob = MetricsEquations.totalJobDelay(0.5, 1.0)
                + MetricsEquations.totalJobDelay(1.5, 3.0);
        assertEquals(expectedTotalJob / 2.0, metrics.getAverageTotalJobDelay(), 1e-9);
        assertEquals(2, metrics.getPartsDelays().size());
    }

    @Test
    void partsDelaysListIsUnmodifiable() {
        PartsDelayMetrics metrics = new PartsDelayMetrics();
        assertThrows(UnsupportedOperationException.class, () -> metrics.getPartsDelays().add(1.0));
    }
}
