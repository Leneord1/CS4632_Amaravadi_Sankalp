package simulator.analysis;

import simulator.SimulationEngine;
import simulator.config.SimulationConfig;
import simulator.data.DataRecorder;
import simulator.data.RunResultWriter;
import simulator.metrics.MetricsReport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class BatchRunner {
    private static final Logger LOGGER = Logger.getLogger(BatchRunner.class.getName());

    private BatchRunner() {}

    public static List<ReplicationResult> runReplications(
            SimulationConfig baseConfig,
            int replications,
            String experimentLabel,
            String paramsChanged,
            Path sessionDir,
            boolean writePerRunFiles)
            throws IOException {
        if (replications <= 0) {
            throw new IllegalArgumentException("replications must be positive");
        }
        Files.createDirectories(sessionDir);
        RunResultWriter writer = null;
        if (writePerRunFiles) {
            writer = RunResultWriter.createSession(sessionDir);
        }

        List<ReplicationResult> results = new ArrayList<>(replications);
        long baseSeed = baseConfig.getRandomSeed();
        for (int i = 1; i <= replications; i++) {
            long seed = baseSeed + (i - 1L);
            SimulationConfig config =
                    baseConfig.toBuilder().randomSeed(seed).replicationCount(1).build();
            DataRecorder recorder = new DataRecorder();
            long startNanos = System.nanoTime();
            SimulationEngine engine = new SimulationEngine(config, recorder);
            engine.run();
            long wallClockMillis = (System.nanoTime() - startNanos) / 1_000_000L;
            MetricsReport report = engine.getMetrics().buildReport();
            ReplicationResult result =
                    new ReplicationResult(
                            i, seed, wallClockMillis, experimentLabel, paramsChanged, report);
            results.add(result);
            if (writer != null) {
                writer.writeRun(
                        i,
                        experimentLabel,
                        paramsChanged,
                        wallClockMillis,
                        config,
                        report,
                        recorder);
            }
        }
        if (LOGGER.isLoggable(java.util.logging.Level.INFO)) {
            LOGGER.info(
                    () ->
                            String.format(
                                    Locale.US,
                                    "Batch complete: %s (%s) n=%d",
                                    experimentLabel,
                                    paramsChanged,
                                    replications));
        }
        return results;
    }

    public static Map<String, MetricAggregate> aggregate(List<ReplicationResult> results) {
        Map<String, MetricAggregate> out = new LinkedHashMap<>();
        out.put(
                "avg_customer_wait_h",
                MetricAggregate.of(
                        "avg_customer_wait_h",
                        results.stream().mapToDouble(ReplicationResult::getAverageCustomerWaitHours).toArray()));
        out.put(
                "avg_advisor_wait_h",
                MetricAggregate.of(
                        "avg_advisor_wait_h",
                        results.stream().mapToDouble(ReplicationResult::getAverageAdvisorWaitHours).toArray()));
        out.put(
                "avg_queue_delay_h",
                MetricAggregate.of(
                        "avg_queue_delay_h",
                        results.stream().mapToDouble(ReplicationResult::getAverageQueueDelayHours).toArray()));
        out.put(
                "avg_parts_delay_h",
                MetricAggregate.of(
                        "avg_parts_delay_h",
                        results.stream().mapToDouble(ReplicationResult::getAveragePartsDelayHours).toArray()));
        out.put(
                "avg_service_time_h",
                MetricAggregate.of(
                        "avg_service_time_h",
                        results.stream().mapToDouble(ReplicationResult::getAverageServiceTimeHours).toArray()));
        out.put(
                "shop_tech_util",
                MetricAggregate.of(
                        "shop_tech_util",
                        results.stream()
                                .mapToDouble(ReplicationResult::getShopTechnicianUtilization)
                                .toArray()));
        out.put(
                "shop_bay_util",
                MetricAggregate.of(
                        "shop_bay_util",
                        results.stream().mapToDouble(ReplicationResult::getShopBayUtilization).toArray()));
        out.put(
                "jobs_completed",
                MetricAggregate.of(
                        "jobs_completed",
                        results.stream().mapToDouble(r -> r.getJobsCompletedPerDay()).toArray()));
        out.put(
                "analytical_rho",
                MetricAggregate.of(
                        "analytical_rho",
                        results.stream()
                                .mapToDouble(ReplicationResult::getAnalyticalSystemUtilization)
                                .toArray()));
        out.put(
                "util_rel_error",
                MetricAggregate.of(
                        "util_rel_error",
                        results.stream()
                                .mapToDouble(ReplicationResult::getUtilizationRelativeError)
                                .toArray()));
        return out;
    }

    public static void writeReplicationCsv(Path path, List<ReplicationResult> results) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(ReplicationResult.csvHeader()).append(System.lineSeparator());
        for (ReplicationResult result : results) {
            sb.append(result.toCsvRow()).append(System.lineSeparator());
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    public static void appendReplicationCsv(Path path, List<ReplicationResult> results) throws IOException {
        boolean exists = Files.exists(path);
        StringBuilder sb = new StringBuilder();
        if (!exists) {
            sb.append(ReplicationResult.csvHeader()).append(System.lineSeparator());
        }
        for (ReplicationResult result : results) {
            sb.append(result.toCsvRow()).append(System.lineSeparator());
        }
        if (exists) {
            Files.writeString(
                    path,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.APPEND);
        } else {
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
        }
    }

    public static void writeStatsCsv(Path path, String experiment, Map<String, MetricAggregate> stats)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("experiment,").append(MetricAggregate.csvHeader()).append(System.lineSeparator());
        for (MetricAggregate aggregate : stats.values()) {
            sb.append(experiment).append(',').append(aggregate.toCsvRow()).append(System.lineSeparator());
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }
}
