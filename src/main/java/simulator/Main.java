package simulator;

import java.util.logging.Logger;
import simulator.config.SimulationConfig;
import simulator.config.SimulationConfigLoader;
import simulator.metrics.MetricsCollector;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            SimulationConfig config = SimulationConfigLoader.load(args);
            run(config);
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("Usage:")) {
                LOGGER.info(exception.getMessage());
                return;
            }
            throw exception;
        }
    }

    static void run(SimulationConfig config) {
        LOGGER.info("Service Department Operational Optimization Simulator");
        LOGGER.info(formatConfig(config));

        MetricsCollector metricsCollector = new MetricsCollector();
        metricsCollector.configureSimulation(config);
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
