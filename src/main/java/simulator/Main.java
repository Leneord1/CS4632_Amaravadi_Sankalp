package simulator;

import simulator.config.SimulationConfig;
import simulator.config.SimulationConfigLoader;
import simulator.data.DataRecorder;
import simulator.data.RunResultWriter;
import simulator.metrics.MetricsReport;
import simulator.analysis.BatchRunner;
import simulator.analysis.ReplicationResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String OUTPUT_PREFIX = "--output=";
    private static final String DEFAULT_OUTPUT_DIR = "results";

    public static void main(String[] args) {
        try {
            SimulationConfig config = SimulationConfigLoader.load(args);
            Path outputDir = Path.of(readOption(args, OUTPUT_PREFIX, DEFAULT_OUTPUT_DIR));
            if (!hasFlag(args, "--no-prompt")) {
                config = promptForSettings(config);
            }
            run(config, outputDir);
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("Usage:")) {
                LOGGER.info(exception.getMessage());
                return;
            }
            throw exception;
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to write simulation output", exception);
        }
    }

    private static SimulationConfig promptForSettings(SimulationConfig config) {
        // Prompt the user for technicians, advisors, and customers.
        java.util.Scanner userInput = new java.util.Scanner(System.in);
        int technicianCount = readInt(userInput, "Number of technicians", 1);
        int advisorCount = readInt(userInput, "Number of advisors", 1);
        int customerCount = readInt(userInput, "Number of customers", 0);
        return config.toBuilder()
                .technicianCount(technicianCount)
                .advisorCount(advisorCount)
                .customerCount(customerCount)
                .build();
    }

    private static int readInt(java.util.Scanner scanner, String label, int minValue) {
        while (true) {
            LOGGER.info(() -> String.format("%s [%d]: ", label, minValue));
            if (!scanner.hasNextLine()) {
                return minValue;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return minValue;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= minValue) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                LOGGER.info(() -> "Invalid input. Please enter a whole number.");
            }
            LOGGER.info(() -> String.format("Enter a whole number >= %d.", minValue));
        }
    }

    static void run(SimulationConfig config, Path outputDir) throws IOException {
        // Run one or more replications (seed + i) and export data files.
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Service Department Operational Optimization Simulator");
            LOGGER.info(formatConfig(config));
        }

        int replications = Math.max(1, config.getReplicationCount());
        if (replications == 1) {
            DataRecorder recorder = new DataRecorder();
            long startNanos = System.nanoTime();
            SimulationEngine engine = new SimulationEngine(config, recorder);
            engine.run();
            long wallClockMillis = (System.nanoTime() - startNanos) / 1_000_000L;
            MetricsReport report = engine.getMetrics().buildReport();
            RunResultWriter writer = RunResultWriter.createSession(outputDir);
            writer.writeRun(1, "CLI single run", "interactive", wallClockMillis, config, report, recorder);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Results written to " + writer.getSessionDir());
            }
            return;
        }

        List<ReplicationResult> results =
                BatchRunner.runReplications(
                        config,
                        replications,
                        "cli_batch",
                        "replications=" + replications,
                        outputDir,
                        true);
        BatchRunner.writeReplicationCsv(outputDir.resolve("aggregate_replications.csv"), results);
        BatchRunner.writeStatsCsv(
                outputDir.resolve("aggregate_stats.csv"), "cli_batch", BatchRunner.aggregate(results));
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Batch results written under " + outputDir.toAbsolutePath());
        }
    }

    private static String readOption(String[] args, String prefix, String defaultValue) {
        for (String arg : args) {
            if (arg.startsWith(prefix)) {
                return arg.substring(prefix.length());
            }
        }
        return defaultValue;
    }

    private static boolean hasFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static String formatConfig(SimulationConfig config) {
        return String.format(
                "Config horizon=%.1fh lambda=%.2f c=%d mu=%.2f advisors=%d alpha=%.2f k=%.1f"
                        + " reorder(r=%d,Q=%d,L=%.1fh) tolerance=%.2f replications=%d seed=%d model=%s",
                config.getSimulationHorizonHours(),
                config.getArrivalRate(),
                config.getTechnicianCount(),
                config.getServiceRatePerTechnician(),
                config.getAdvisorCount(),
                config.getExperienceAlpha(),
                config.getGammaShapeParameter(),
                config.getPartsReorderPoint(),
                config.getPartsReorderQuantity(),
                config.getPartsLeadTimeHours(),
                config.getValidationRelativeTolerance(),
                config.getReplicationCount(),
                config.getRandomSeed(),
                config.getServiceTimeModel());
    }
}
