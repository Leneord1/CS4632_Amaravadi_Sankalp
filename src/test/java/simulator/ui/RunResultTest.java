package simulator.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.metrics.MetricsReport;

class RunResultTest {

    @Test
    void recordExposesAllComponents() {
        SimulationConfig config = SimulationConfig.defaults();
        MetricsReport report = new MetricsReport();
        DataRecorder recorder = new DataRecorder();

        RunResult result = new RunResult(config, report, recorder, 1234L);

        assertSame(config, result.config());
        assertSame(report, result.report());
        assertSame(recorder, result.recorder());
        assertEquals(1234L, result.wallClockMillis());
    }
}
