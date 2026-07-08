package simulator.ui;

import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.metrics.MetricsReport;

public record RunResult(
        SimulationConfig config,
        MetricsReport report,
        DataRecorder recorder,
        long wallClockMillis) {}
