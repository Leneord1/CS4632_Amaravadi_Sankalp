package simulator.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueBenchmarkMetricsTest {
    @Test
    void matchesPdfScenarioUtilization() {
        QueueBenchmarkMetrics metrics = new QueueBenchmarkMetrics();
        assertEquals(0.667, metrics.calculateSystemUtilization(4.0, 3, 2.0), 1e-3);
    }

    @Test
    void matchesPdfScenarioQueueWait() {
        QueueBenchmarkMetrics metrics = new QueueBenchmarkMetrics();
        assertEquals(2.0 / 9.0, metrics.calculateExpectedQueueWait(4.0, 3, 2.0), 1e-3);
    }

    @Test
    void expectedQueueWaitIsPositiveForStableSystem() {
        QueueBenchmarkMetrics metrics = new QueueBenchmarkMetrics();
        assertTrue(metrics.calculateExpectedQueueWait(4.0, 3, 2.0) > 0.0);
    }

    @Test
    void probabilityOfWaitingIsBetweenZeroAndOne() {
        QueueBenchmarkMetrics metrics = new QueueBenchmarkMetrics();
        double probability = metrics.calculateProbabilityOfWaiting(4.0, 3, 2.0);
        assertTrue(probability > 0.0);
        assertTrue(probability < 1.0);
    }
}
