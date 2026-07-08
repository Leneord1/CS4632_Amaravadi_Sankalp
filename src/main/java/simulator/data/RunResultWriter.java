package simulator.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import simulator.config.SimulationConfig;
import simulator.data.JsonWriter.JsonObject;
import simulator.metrics.MetricsReport;
import simulator.metrics.ValidationReport;
import simulator.model.RepairBay;
import simulator.model.Technician;

public final class RunResultWriter {
    private static final DateTimeFormatter DIR_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String MASTER_INDEX = "master_index.csv";
    private static final String MASTER_HEADER =
            "run_id,purpose,params_changed,duration_seconds,status,"
                    + "timeseries_file,events_file,summary_file,config_file";

    private final Path sessionDir;
    private final Path masterIndex;

    private RunResultWriter(Path sessionDir) {
        this.sessionDir = sessionDir;
        this.masterIndex = sessionDir.resolve(MASTER_INDEX);
    }

    public static RunResultWriter createSession(Path resultsRoot) throws IOException {
        String timestamp = LocalDateTime.now(ZoneId.systemDefault()).format(DIR_FORMAT);
        Path sessionDir = resultsRoot.resolve("run_" + timestamp);
        Files.createDirectories(sessionDir);
        RunResultWriter writer = new RunResultWriter(sessionDir);
        Files.writeString(writer.masterIndex, MASTER_HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
        return writer;
    }

    public Path getSessionDir() {
        return sessionDir;
    }

    public void writeRun(
            int runId,
            String purpose,
            String paramsChanged,
            long wallClockMillis,
            SimulationConfig config,
            MetricsReport report,
            DataRecorder recorder) throws IOException {
        String base = String.format("run_%03d", runId);
        CsvWriter.writeTimeSeries(sessionDir.resolve(base + "_timeseries.csv"), recorder.getSamples());
        CsvWriter.writeEvents(sessionDir.resolve(base + "_events.csv"), recorder.getEvents());
        JsonWriter.write(
                sessionDir.resolve(base + "_summary.json"),
                buildSummaryJson(runId, purpose, wallClockMillis, report).toString());
        JsonWriter.write(
                sessionDir.resolve(base + "_config.json"),
                buildConfigJson(config).toString());
        appendMasterIndex(runId, purpose, paramsChanged, wallClockMillis, base);
    }

    private static JsonObject buildSummaryJson(
            int runId, String purpose, long wallClockMillis, MetricsReport report) {
        JsonObject techUtil = JsonWriter.object();
        for (Map.Entry<Technician, Double> entry : report.getTechnicianUtilization().entrySet()) {
            techUtil.put(Integer.toString(entry.getKey().getTechnicianId()), entry.getValue());
        }
        JsonObject bayUtil = JsonWriter.object();
        for (Map.Entry<RepairBay, Double> entry : report.getBayUtilization().entrySet()) {
            bayUtil.put(Integer.toString(entry.getKey().getBayId()), entry.getValue());
        }

        JsonObject summary = JsonWriter.object()
                .put("runId", runId)
                .put("purpose", purpose)
                .put("wallClockMillis", wallClockMillis)
                .put("jobsCompletedPerDay", report.getJobsCompletedPerDay())
                .put("averageCustomerWaitHours", report.getAverageCustomerWaitTime())
                .put("averageAdvisorWaitHours", report.getAverageAdvisorWaitTime())
                .put("averageQueueDelayHours", report.getAverageQueueDelay())
                .put("averagePartsDelayHours", report.getAveragePartsDelay())
                .put("averageServiceTimeHours", report.getAverageServiceTime())
                .put("averageTotalJobDelayHours", report.getAverageTotalJobDelay())
                .put("shopTechnicianUtilization", report.getSimulatedShopTechnicianUtilization())
                .put("shopBayUtilization", report.getSimulatedShopBayUtilization())
                .put("analyticalSystemUtilization", report.getAnalyticalSystemUtilization())
                .put("analyticalQueueWaitHours", report.getAnalyticalQueueWait())
                .put("technicianUtilization", techUtil)
                .put("bayUtilization", bayUtil);

        ValidationReport validation = report.getValidationReport();
        if (validation != null) {
            summary.put("validation", JsonWriter.object()
                    .put("utilizationRelativeError", validation.getUtilizationRelativeError())
                    .put("queueWaitRelativeError", validation.getQueueWaitRelativeError())
                    .put("overallValid", validation.isOverallValid()));
        }
        return summary;
    }

    private static JsonObject buildConfigJson(SimulationConfig config) {
        return JsonWriter.object()
                .put("randomSeed", config.getRandomSeed())
                .put("simulationHorizonHours", config.getSimulationHorizonHours())
                .put("arrivalRate", config.getArrivalRate())
                .put("arrivalProfile", config.getArrivalProfile().name())
                .put("technicianCount", config.getTechnicianCount())
                .put("serviceRatePerTechnician", config.getServiceRatePerTechnician())
                .put("advisorCount", config.getAdvisorCount())
                .put("experienceAlpha", config.getExperienceAlpha())
                .put("maxExperienceLevel", config.getMaxExperienceLevel())
                .put("gammaShapeParameter", config.getGammaShapeParameter())
                .put("serviceTimeModel", config.getServiceTimeModel().name())
                .put("partsReorderPoint", config.getPartsReorderPoint())
                .put("partsReorderQuantity", config.getPartsReorderQuantity())
                .put("partsLeadTimeHours", config.getPartsLeadTimeHours())
                .put("initialPartsQuantityOnHand", config.getInitialPartsQuantityOnHand())
                .put("validationRelativeTolerance", config.getValidationRelativeTolerance())
                .put("replicationCount", config.getReplicationCount());
    }

    private void appendMasterIndex(
            int runId, String purpose, String paramsChanged, long wallClockMillis, String base) throws IOException {
        String row = runId + ","
                + CsvWriter.escape(purpose) + ","
                + CsvWriter.escape(paramsChanged) + ","
                + (wallClockMillis / 1000.0) + ","
                + "Complete" + ","
                + base + "_timeseries.csv" + ","
                + base + "_events.csv" + ","
                + base + "_summary.json" + ","
                + base + "_config.json";
        try (BufferedWriter writer = Files.newBufferedWriter(
                masterIndex, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(row);
            writer.newLine();
        }
    }
}
