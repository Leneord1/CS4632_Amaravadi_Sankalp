package simulator;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulator.config.SimulationConfig;
import simulator.config.SimulationConfigLoader;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            SimulationConfig config = SimulationConfigLoader.load(args);
            config = promptForSettings(config);
            run(config);
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("Usage:")) {
                LOGGER.info(exception.getMessage());
                return;
            }
            throw exception;
        }
    }

    private static SimulationConfig promptForSettings(SimulationConfig config) {
        Scanner userInput = new Scanner(System.in);
        int technicianCount = readInt(userInput, "Number of technicians", 1);
        int advisorCount = readInt(userInput, "Number of advisors", 1);
        int customerCount = readInt(userInput, "Number of customers", 0);
        return config.toBuilder()
                .technicianCount(technicianCount)
                .advisorCount(advisorCount)
                .customerCount(customerCount)
                .build();
    }

    private static int readInt(Scanner scanner, String label, int minValue) {
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

    static void run(SimulationConfig config) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Service Department Operational Optimization Simulator");
            LOGGER.info(formatConfig(config));
        }

        SimulationEngine engine = new SimulationEngine(config);
        engine.run();
    }

    private static String formatConfig(SimulationConfig config) {
        return String.format(
                "Config horizon=%.1fh lambda=%.2f c=%d mu=%.2f advisors=%d alpha=%.2f k=%.1f "
                        + "reorder(r=%d,Q=%d,L=%.1fh) tolerance=%.2f replications=%d seed=%d model=%s",
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
