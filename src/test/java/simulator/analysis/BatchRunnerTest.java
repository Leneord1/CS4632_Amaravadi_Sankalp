package simulator.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import simulator.config.SimulationConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class BatchRunnerTest {

    @Test
    void metricAggregateComputesMeanStdAndCi() {
        MetricAggregate a = MetricAggregate.of("x", new double[] {10.0, 12.0, 14.0});
        assertEquals(12.0, a.getMean(), 1e-9);
        assertEquals(10.0, a.getMin(), 1e-9);
        assertEquals(14.0, a.getMax(), 1e-9);
        assertTrue(a.getStdDev() > 0.0);
        assertTrue(a.getCiHigh() > a.getCiLow());
    }

    @Test
    void sensitivityMatchesAppendixFormula() {
        // 10% input increase -> 25% output increase => 2.5
        double s = MetricAggregate.sensitivity(100.0, 110.0, 40.0, 50.0);
        assertEquals(2.5, s, 1e-9);
    }

    @Test
    void runReplicationsWritesAggregateRows(@TempDir Path dir) throws IOException {
        SimulationConfig config =
                SimulationConfig.builder()
                        .simulationHorizonHours(2.0)
                        .customerCount(4)
                        .randomSeed(7L)
                        .build();
        List<ReplicationResult> results =
                BatchRunner.runReplications(config, 3, "unit", "n=3", dir, false);
        assertEquals(3, results.size());
        assertEquals(7L, results.get(0).getSeed());
        assertEquals(8L, results.get(1).getSeed());
        Map<String, MetricAggregate> stats = BatchRunner.aggregate(results);
        assertTrue(stats.containsKey("avg_customer_wait_h"));
        Path csv = dir.resolve("rows.csv");
        BatchRunner.writeReplicationCsv(csv, results);
        assertTrue(Files.size(csv) > 0);
    }
}
