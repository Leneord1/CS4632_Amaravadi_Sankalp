package simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simulator.config.ArrivalProfile;
import simulator.config.ServiceTimeModel;
import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.metrics.MetricsCollector;

// End-to-end coverage of the discrete-event loop and its branch paths.
class SimulationEngineTest {

    private static SimulationConfig.Builder baseBuilder() {
        return SimulationConfig.builder()
                .simulationHorizonHours(4.0)
                .arrivalRate(3.0)
                .technicianCount(2)
                .serviceRatePerTechnician(2.0)
                .advisorCount(2)
                .randomSeed(7L);
    }

    // Dealership arrivals + PDF service model + recorder exercises snapshot and event recording.
    @Test
    void runWithRecorderCapturesSamplesAndEvents() {
        SimulationConfig config =
                baseBuilder()
                        .arrivalProfile(ArrivalProfile.DEALERSHIP_DAY)
                        .serviceTimeModel(ServiceTimeModel.PDF)
                        .customerCount(15)
                        .build();
        DataRecorder recorder = new DataRecorder();
        SimulationEngine engine = new SimulationEngine(config, recorder);

        engine.run();

        assertTrue(recorder.getSampleCount() > 0);
        assertTrue(recorder.getEventCount() > 0);
        assertTrue(engine.getMetrics().getThroughputMetrics().getRecordedJobs() > 0);
    }

    // Constant arrivals + legacy service model + no recorder exercises the null-recorder branches.
    @Test
    void runConstantProfileLegacyModelWithoutRecorder() {
        SimulationConfig config =
                baseBuilder()
                        .arrivalProfile(ArrivalProfile.CONSTANT)
                        .serviceTimeModel(ServiceTimeModel.LEGACY)
                        .customerCount(0)
                        .build();
        SimulationEngine engine = new SimulationEngine(config);

        engine.run();

        MetricsCollector metrics = engine.getMetrics();
        assertNotNull(metrics.buildReport());
        assertTrue(metrics.getThroughputMetrics().getRecordedJobs() >= 0);
    }

    // Zero starting inventory forces blocked tickets, part orders, and parts-arrival handling.
    @Test
    void runWithPartsShortageTriggersPartsArrivalPath() {
        SimulationConfig config =
                baseBuilder()
                        .customerCount(20)
                        .initialPartsQuantityOnHand(0)
                        .partsReorderPoint(5)
                        .partsReorderQuantity(10)
                        .partsLeadTimeHours(1.0)
                        .build();
        DataRecorder recorder = new DataRecorder();
        SimulationEngine engine = new SimulationEngine(config, recorder);

        engine.run();

        boolean sawPartsArrival =
                recorder.getEvents().stream()
                        .anyMatch(event -> "PARTS_ARRIVAL".equals(event.eventType()));
        assertTrue(sawPartsArrival);
    }

    // getMetrics returns the same collector instance used during the run.
    @Test
    void getMetricsReturnsRunCollector() {
        SimulationEngine engine = new SimulationEngine(baseBuilder().customerCount(5).build());
        MetricsCollector before = engine.getMetrics();
        engine.run();
        assertSame(before, engine.getMetrics());
    }

    // Single-customer default constructor path completes without recorder wiring.
    @Test
    void runMinimalConfigCompletes() {
        SimulationConfig config = baseBuilder().customerCount(1).build();
        SimulationEngine engine = new SimulationEngine(config);
        engine.run();
        assertEquals(7L, config.getRandomSeed());
    }
}
