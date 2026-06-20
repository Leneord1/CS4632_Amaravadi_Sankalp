package simulator.metrics;

import org.junit.jupiter.api.Test;
import simulator.model.RepairBay;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BayUtilizationMetricsTest {
    @Test
    void calculatesBayAndShopUtilization() {
        BayUtilizationMetrics metrics = new BayUtilizationMetrics();
        metrics.setSimulationHorizonHours(10.0);

        RepairBay bayOne = new RepairBay(1);
        RepairBay bayTwo = new RepairBay(2);
        metrics.recordOccupiedTime(bayOne, 6.0);
        metrics.recordOccupiedTime(bayTwo, 4.0);

        assertEquals(0.6, metrics.getBayUtilization(bayOne), 1e-9);
        assertEquals(0.4, metrics.getBayUtilization(bayTwo), 1e-9);
        assertEquals(0.5, metrics.getShopBayUtilization(), 1e-9);
    }
}
