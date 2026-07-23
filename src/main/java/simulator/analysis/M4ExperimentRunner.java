package simulator.analysis;

import simulator.config.SimulationConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class M4ExperimentRunner {
    private static final Logger LOGGER = Logger.getLogger(M4ExperimentRunner.class.getName());
    private static final int DEFAULT_REPLICATIONS = 30;
    private static final String DEFAULT_OUTPUT = "results/m4";

    private M4ExperimentRunner() {}

    public static void main(String[] args) throws IOException {
        int replications = readIntOption(args, "--replications=", DEFAULT_REPLICATIONS);
        Path outputRoot = Path.of(readOption(args, "--output=", DEFAULT_OUTPUT));
        Files.createDirectories(outputRoot);
        boolean writePerRun = hasFlag(args, "--write-per-run");

        LOGGER.info(() -> "M4 experiment output: " + outputRoot.toAbsolutePath());
        LOGGER.info(() -> "Replications per case: " + replications);

        Path allReplications = outputRoot.resolve("all_replications.csv");
        Files.deleteIfExists(allReplications);

        SimulationConfig baseline = baselineConfig();
        Map<String, Map<String, MetricAggregate>> caseStats = new LinkedHashMap<>();

        List<ReplicationResult> baselineRuns =
                runCase(
                        baseline,
                        replications,
                        "baseline",
                        "defaults",
                        outputRoot,
                        allReplications,
                        writePerRun,
                        caseStats);

        runSensitivity(
                baseline, replications, outputRoot, allReplications, writePerRun, caseStats);

        runScenarios(baseline, replications, outputRoot, allReplications, writePerRun, caseStats);

        runValidationCases(
                baseline, replications, outputRoot, allReplications, writePerRun, caseStats);

        writeStatisticalSummary(outputRoot.resolve("statistical_summary.csv"), caseStats);
        writeSensitivityTable(outputRoot, caseStats);
        writeScenarioTable(outputRoot, caseStats);
        writeValidationEvidence(outputRoot, caseStats, baselineRuns);

        LOGGER.info("M4 Phases 1–5 complete. See " + outputRoot.toAbsolutePath());
    }

    private static SimulationConfig baselineConfig() {
        return SimulationConfig.builder()
                .simulationHorizonHours(10.0)
                .arrivalRate(4.0)
                .technicianCount(3)
                .serviceRatePerTechnician(2.0)
                .advisorCount(4)
                .gammaShapeParameter(4.0)
                .experienceAlpha(1.0)
                .partsReorderPoint(5)
                .partsReorderQuantity(10)
                .partsLeadTimeHours(2.0)
                .initialPartsQuantityOnHand(20)
                .randomSeed(42L)
                .replicationCount(1)
                .build();
    }

    private static List<ReplicationResult> runCase(
            SimulationConfig config,
            int replications,
            String label,
            String paramsChanged,
            Path outputRoot,
            Path allReplications,
            boolean writePerRun,
            Map<String, Map<String, MetricAggregate>> caseStats)
            throws IOException {
        Path caseDir = outputRoot.resolve("cases").resolve(sanitize(label));
        List<ReplicationResult> results =
                BatchRunner.runReplications(
                        config, replications, label, paramsChanged, caseDir, writePerRun);
        BatchRunner.appendReplicationCsv(allReplications, results);
        Map<String, MetricAggregate> stats = BatchRunner.aggregate(results);
        caseStats.put(label, stats);
        BatchRunner.writeStatsCsv(outputRoot.resolve("stats_" + sanitize(label) + ".csv"), label, stats);
        return results;
    }

    private static void runSensitivity(
            SimulationConfig baseline,
            int replications,
            Path outputRoot,
            Path allReplications,
            boolean writePerRun,
            Map<String, Map<String, MetricAggregate>> caseStats)
            throws IOException {
        // Arrival rate
        for (double lambda : new double[] {2.0, 6.0, 8.0}) {
            runCase(
                    baseline.toBuilder().arrivalRate(lambda).build(),
                    replications,
                    "sens_lambda_" + formatNum(lambda),
                    "arrivalRate=" + lambda,
                    outputRoot,
                    allReplications,
                    writePerRun,
                    caseStats);
        }
        // Technicians/bays
        for (int c : new int[] {2, 4, 5}) {
            runCase(
                    baseline.toBuilder().technicianCount(c).build(),
                    replications,
                    "sens_techs_" + c,
                    "technicianCount=" + c,
                    outputRoot,
                    allReplications,
                    writePerRun,
                    caseStats);
        }
        // Service rate
        for (double mu : new double[] {1.5, 2.5}) {
            runCase(
                    baseline.toBuilder().serviceRatePerTechnician(mu).build(),
                    replications,
                    "sens_mu_" + formatNum(mu),
                    "serviceRate=" + mu,
                    outputRoot,
                    allReplications,
                    writePerRun,
                    caseStats);
        }
        // Advisors
        for (int a : new int[] {1, 2}) {
            runCase(
                    baseline.toBuilder().advisorCount(a).build(),
                    replications,
                    "sens_advisors_" + a,
                    "advisorCount=" + a,
                    outputRoot,
                    allReplications,
                    writePerRun,
                    caseStats);
        }
        // Parts lead time
        for (double lead : new double[] {0.5, 4.0}) {
            runCase(
                    baseline.toBuilder().partsLeadTimeHours(lead).build(),
                    replications,
                    "sens_parts_lead_" + formatNum(lead),
                    "partsLeadTimeHours=" + lead,
                    outputRoot,
                    allReplications,
                    writePerRun,
                    caseStats);
        }
    }

    private static void runScenarios(
            SimulationConfig baseline,
            int replications,
            Path outputRoot,
            Path allReplications,
            boolean writePerRun,
            Map<String, Map<String, MetricAggregate>> caseStats)
            throws IOException {
        // Normal already covered by baseline
        runCase(
                baseline.toBuilder().arrivalRate(8.0).build(),
                replications,
                "scenario_peak",
                "arrivalRate=8 (peak rush)",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        runCase(
                baseline.toBuilder().technicianCount(2).advisorCount(1).build(),
                replications,
                "scenario_understaffed",
                "technicians=2,advisors=1",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        runCase(
                baseline.toBuilder()
                        .partsLeadTimeHours(4.0)
                        .partsReorderPoint(2)
                        .initialPartsQuantityOnHand(5)
                        .build(),
                replications,
                "scenario_parts_stress",
                "lead=4,reorder=2,initialQty=5",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        runCase(
                baseline.toBuilder().technicianCount(5).advisorCount(6).arrivalRate(4.0).build(),
                replications,
                "scenario_ideal_capacity",
                "technicians=5,advisors=6",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
    }

    private static void runValidationCases(
            SimulationConfig baseline,
            int replications,
            Path outputRoot,
            Path allReplications,
            boolean writePerRun,
            Map<String, Map<String, MetricAggregate>> caseStats)
            throws IOException {
        // Near-empty arrivals
        runCase(
                baseline.toBuilder().arrivalRate(0.1).build(),
                replications,
                "val_extreme_low_arrival",
                "arrivalRate=0.1",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        // Overloaded scenario
        runCase(
                baseline.toBuilder().arrivalRate(20.0).build(),
                replications,
                "val_extreme_overload",
                "arrivalRate=20",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        // Single technician
        runCase(
                baseline.toBuilder().technicianCount(1).build(),
                replications,
                "val_extreme_one_tech",
                "technicianCount=1",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
        // Instant parts
        runCase(
                baseline.toBuilder().partsLeadTimeHours(0.0).initialPartsQuantityOnHand(100).build(),
                replications,
                "val_instant_parts",
                "lead=0,initialQty=100",
                outputRoot,
                allReplications,
                writePerRun,
                caseStats);
    }

    private static void writeStatisticalSummary(
            Path path, Map<String, Map<String, MetricAggregate>> caseStats) throws IOException {
        String[] metrics = {
            "avg_customer_wait_h",
            "shop_tech_util",
            "avg_parts_delay_h",
            "jobs_completed",
            "avg_advisor_wait_h",
            "avg_queue_delay_h"
        };
        StringBuilder sb = new StringBuilder();
        sb.append("experiment,metric,n,mean,std_dev,min,max,ci95_low,ci95_high")
                .append(System.lineSeparator());
        for (Map.Entry<String, Map<String, MetricAggregate>> entry : caseStats.entrySet()) {
            for (String metric : metrics) {
                MetricAggregate a = entry.getValue().get(metric);
                if (a == null) {
                    continue;
                }
                sb.append(entry.getKey())
                        .append(',')
                        .append(a.toCsvRow())
                        .append(System.lineSeparator());
            }
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private static void writeSensitivityTable(
    //
        Path outputRoot, Map<String, Map<String, MetricAggregate>> caseStats) throws IOException {
        MetricAggregate baseWait = caseStats.get("baseline").get("avg_customer_wait_h");
        MetricAggregate baseUtil = caseStats.get("baseline").get("shop_tech_util");
        MetricAggregate baseJobs = caseStats.get("baseline").get("jobs_completed");

        StringBuilder sb = new StringBuilder();
        sb.append(
                "parameter,level,baseline_level,wait_mean,wait_sensitivity,util_mean,util_sensitivity,"
              + "jobs_mean,jobs_sensitivity")
        .append(System.lineSeparator());

        appendSensitivityRows(
                sb,
                "arrivalRate",
                4.0,
                new String[] {"sens_lambda_2", "sens_lambda_6", "sens_lambda_8"},
                new double[] {2.0, 6.0, 8.0},
                baseWait,
                baseUtil,
                baseJobs,
                caseStats);
        appendSensitivityRows(
                sb,
                "technicianCount",
                3.0,
                new String[] {"sens_techs_2", "sens_techs_4", "sens_techs_5"},
                new double[] {2.0, 4.0, 5.0},
                baseWait,
                baseUtil,
                baseJobs,
                caseStats);
        appendSensitivityRows(
                sb,
                "serviceRate",
                2.0,
                new String[] {"sens_mu_1.5", "sens_mu_2.5"},
                new double[] {1.5, 2.5},
                baseWait,
                baseUtil,
                baseJobs,
                caseStats);
        appendSensitivityRows(
                sb,
                "advisorCount",
                4.0,
                new String[] {"sens_advisors_1", "sens_advisors_2"},
                new double[] {1.0, 2.0},
                baseWait,
                baseUtil,
                baseJobs,
                caseStats);
        appendSensitivityRows(
                sb,
                "partsLeadTimeHours",
                2.0,
                new String[] {"sens_parts_lead_0.5", "sens_parts_lead_4"},
                new double[] {0.5, 4.0},
                baseWait,
                baseUtil,
                baseJobs,
                caseStats);

        Files.writeString(
                outputRoot.resolve("sensitivity_coefficients.csv"),
                sb.toString(),
                StandardCharsets.UTF_8);
    }

    private static void appendSensitivityRows(
            StringBuilder sb,
            String param,
            double baselineLevel,
            String[] labels,
            double[] levels,
            MetricAggregate baseWait,
            MetricAggregate baseUtil,
            MetricAggregate baseJobs,
            Map<String, Map<String, MetricAggregate>> caseStats) {
        for (int i = 0; i < labels.length; i++) {
            Map<String, MetricAggregate> stats = caseStats.get(labels[i]);
            if (stats == null) {
                continue;
            }
            double wait = stats.get("avg_customer_wait_h").getMean();
            double util = stats.get("shop_tech_util").getMean();
            double jobs = stats.get("jobs_completed").getMean();
            double waitSens =
                    MetricAggregate.sensitivity(baselineLevel, levels[i], baseWait.getMean(), wait);
            double utilSens =
                    MetricAggregate.sensitivity(baselineLevel, levels[i], baseUtil.getMean(), util);
            double jobsSens =
                    MetricAggregate.sensitivity(baselineLevel, levels[i], baseJobs.getMean(), jobs);
            sb.append(
                    String.format(
                            Locale.US,
                            "%s,%.4f,%.4f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f%n",
                            param,
                            levels[i],
                            baselineLevel,
                            wait,
                            waitSens,
                            util,
                            utilSens,
                            jobs,
                            jobsSens));
        }
    }

    private static void writeScenarioTable(
            Path outputRoot, Map<String, Map<String, MetricAggregate>> caseStats) throws IOException {
        String[] scenarios = {
            "baseline",
            "scenario_peak",
            "scenario_understaffed",
            "scenario_parts_stress",
            "scenario_ideal_capacity"
        };
        StringBuilder sb = new StringBuilder();
        sb.append(
                        "scenario,wait_mean,wait_ci_low,wait_ci_high,util_mean,parts_delay_mean,"
                                + "jobs_mean,advisor_wait_mean,queue_delay_mean")
                .append(System.lineSeparator());
        for (String scenario : scenarios) {
            Map<String, MetricAggregate> stats = caseStats.get(scenario);
            if (stats == null) {
                continue;
            }
            MetricAggregate wait = stats.get("avg_customer_wait_h");
            MetricAggregate util = stats.get("shop_tech_util");
            MetricAggregate parts = stats.get("avg_parts_delay_h");
            MetricAggregate jobs = stats.get("jobs_completed");
            MetricAggregate advisor = stats.get("avg_advisor_wait_h");
            MetricAggregate queue = stats.get("avg_queue_delay_h");
            sb.append(
                    String.format(
                            Locale.US,
                            "%s,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f%n",
                            scenario,
                            wait.getMean(),
                            wait.getCiLow(),
                            wait.getCiHigh(),
                            util.getMean(),
                            parts.getMean(),
                            jobs.getMean(),
                            advisor.getMean(),
                            queue.getMean()));
        }
        Files.writeString(
                outputRoot.resolve("scenario_comparison.csv"), sb.toString(), StandardCharsets.UTF_8);
    }

    private static void writeValidationEvidence(
            Path outputRoot,
            Map<String, Map<String, MetricAggregate>> caseStats,
            List<ReplicationResult> baselineRuns)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# M4 Validation Evidence (Phases 4–5 data)\n\n");
        sb.append("## Face validation expectations vs results\n\n");
        appendFaceCheck(sb, caseStats);
        sb.append("\n## Extreme condition tests\n\n");
        appendExtremeCheck(sb, caseStats);
        sb.append("\n## Parameter validation notes\n\n");
        sb.append("- Baseline: λ=4/h, c=3, μ=2/tech/h, advisors=4, horizon=10h, Gamma k=4, α=1.\n");
        sb.append("- Values align with M3 defaults calibrated for a small-shop illustration.\n");
        sb.append("- Arrival profile DEALERSHIP_DAY models non-stationary daytime demand.\n");
        sb.append("- Real dealership telemetry was not available; parameters are experience-based.\n");
        sb.append("\n## Output comparison vs M/M/c (baseline replications)\n\n");
        appendMmcComparison(sb, baselineRuns, caseStats);
        sb.append("\n## Limitations\n\n");
        sb.append("- No warm-up deletion; metrics include full 10h horizon.\n");
        sb.append("- Analytical M/M/c assumes exponential service and ignores advisors/parts.\n");
        sb.append("- Simulation uses Gamma service times, experience scaling, and parts blocking.\n");
        sb.append("- Relative error to M/M/c is a soft benchmark, not a hard pass/fail of reality.\n");

        Files.writeString(outputRoot.resolve("validation_evidence.md"), sb.toString(), StandardCharsets.UTF_8);

        // Machine-readable validation snapshot
        StringBuilder csv = new StringBuilder();
        csv.append("check,metric,value,pass_heuristic,notes").append(System.lineSeparator());
        double baseWait = caseStats.get("baseline").get("avg_customer_wait_h").getMean();
        double peakWait = caseStats.get("scenario_peak").get("avg_customer_wait_h").getMean();
        double idealWait =
                caseStats.get("scenario_ideal_capacity").get("avg_customer_wait_h").getMean();
        double lowUtil =
                caseStats.get("val_extreme_low_arrival").get("shop_tech_util").getMean();
        double overloadWait =
                caseStats.get("val_extreme_overload").get("avg_customer_wait_h").getMean();
        csv.append(
                String.format(
                        Locale.US,
                        "face_peak_wait_gt_baseline,wait_hours,%.6f,%s,peak vs baseline%n",
                        peakWait,
                        peakWait > baseWait));
        csv.append(
                String.format(
                        Locale.US,
                        "face_ideal_wait_lt_baseline,wait_hours,%.6f,%s,ideal vs baseline%n",
                        idealWait,
                        idealWait < baseWait));
        csv.append(
                String.format(
                        Locale.US,
                        "extreme_low_arrival_util_near_zero,shop_tech_util,%.6f,%s,expect util << baseline%n",
                        lowUtil,
                        lowUtil < caseStats.get("baseline").get("shop_tech_util").getMean() * 0.25));
        csv.append(
                String.format(
                        Locale.US,
                        "extreme_overload_wait_grows,wait_hours,%.6f,%s,expect large waits%n",
                        overloadWait,
                        overloadWait > baseWait * 2.0));
        Files.writeString(
                outputRoot.resolve("validation_checks.csv"), csv.toString(), StandardCharsets.UTF_8);
    }

    private static void appendFaceCheck(
        StringBuilder sb, Map<String, Map<String, MetricAggregate>> caseStats) {
        double baseWait = caseStats.get("baseline").get("avg_customer_wait_h").getMean();
        double peakWait = caseStats.get("scenario_peak").get("avg_customer_wait_h").getMean();
        double underWait =
                caseStats.get("scenario_understaffed").get("avg_customer_wait_h").getMean();
        double idealWait =
                caseStats.get("scenario_ideal_capacity").get("avg_customer_wait_h").getMean();
        double partsDelay =
                caseStats.get("scenario_parts_stress").get("avg_parts_delay_h").getMean();
        double baseParts = caseStats.get("baseline").get("avg_parts_delay_h").getMean();
        sb.append(
                String.format(
                        Locale.US,
                        "- Peak rush wait (%.3fh) %s baseline (%.3fh).%n",
                        peakWait,
                        peakWait > baseWait ? ">" : "<=",
                        baseWait));
        sb.append(
                String.format(
                        Locale.US,
                        "- Understaffed wait (%.3fh) %s baseline (%.3fh).%n",
                        underWait,
                        underWait > baseWait ? ">" : "<=",
                        baseWait));
        sb.append(
                String.format(
                        Locale.US,
                        "- Ideal capacity wait (%.3fh) %s baseline (%.3fh).%n",
                        idealWait,
                        idealWait < baseWait ? "<" : ">=",
                        baseWait));
        sb.append(
                String.format(
                        Locale.US,
                        "- Parts-stress Dparts (%.3fh) %s baseline (%.3fh).%n",
                        partsDelay,
                        partsDelay >= baseParts ? ">=" : "<",
                        baseParts));
    }

    private static void appendExtremeCheck(
        StringBuilder sb, Map<String, Map<String, MetricAggregate>> caseStats) {
        sb.append(
                String.format(
                        Locale.US,
                        "- Low arrival util mean=%.4f (expect near idle).%n",
                        caseStats.get("val_extreme_low_arrival").get("shop_tech_util").getMean()));
        sb.append(
                String.format(
                        Locale.US,
                        "- Overload wait mean=%.4fh jobs=%.1f (expect long waits / backlog).%n",
                        caseStats.get("val_extreme_overload").get("avg_customer_wait_h").getMean(),
                        caseStats.get("val_extreme_overload").get("jobs_completed").getMean()));
        sb.append(
                String.format(
                        Locale.US,
                        "- One-tech wait mean=%.4fh util=%.4f.%n",
                        caseStats.get("val_extreme_one_tech").get("avg_customer_wait_h").getMean(),
                        caseStats.get("val_extreme_one_tech").get("shop_tech_util").getMean()));
        sb.append(
                String.format(
                        Locale.US,
                        "- Instant parts Dparts mean=%.4fh (expect near 0).%n",
                        caseStats.get("val_instant_parts").get("avg_parts_delay_h").getMean()));
    }

    private static void appendMmcComparison(
            StringBuilder sb,
            List<ReplicationResult> baselineRuns,
            Map<String, Map<String, MetricAggregate>> caseStats) {
        MetricAggregate utilErr = caseStats.get("baseline").get("util_rel_error");
        MetricAggregate rho = caseStats.get("baseline").get("analytical_rho");
        MetricAggregate simUtil = caseStats.get("baseline").get("shop_tech_util");
        long validCount =
                baselineRuns.stream().filter(ReplicationResult::isValidationOverallValid).count();
        sb.append(
                String.format(
                        Locale.US,
                        "- Analytical ρ mean=%.4f; simulated shop util mean=%.4f.%n",
                        rho.getMean(),
                        simUtil.getMean()));
        sb.append(
                String.format(
                        Locale.US,
                        "- Utilization relative error mean=%.4f (std=%.4f), 95%% CI [%.4f, %.4f].%n",
                        utilErr.getMean(),
                        utilErr.getStdDev(),
                        utilErr.getCiLow(),
                        utilErr.getCiHigh()));
        sb.append(
                String.format(
                        Locale.US,
                        "- Baseline replications with overallValid=true: %d / %d.%n",
                        validCount,
                        baselineRuns.size()));
        sb.append(
                "- Caveat: disagreement is expected when advisors, Gamma service, and parts blocking dominate.\n");
    }

    private static String sanitize(String label) {
        return label.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String formatNum(double value) {
        if (Math.rint(value) == value) {
            return Integer.toString((int) value);
        }
        return String.format(Locale.US, "%.1f", value);
    }

    private static String readOption(String[] args, String prefix, String defaultValue) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        return defaultValue;
    }

    private static int readIntOption(String[] args, String prefix, int defaultValue) {
        String raw = readOption(args, prefix, null);
        if (raw == null) {
            return defaultValue;
        }
        return Integer.parseInt(raw);
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
