// Entry point for the service department simulator.
import java.util.logging.Logger;

package simulator;

public class Main {
    Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        logger.info("Starting Service Department Simulator...");
        ServiceDepartmentSimulator simulator = new ServiceDepartmentSimulator();
        simulator.runSimulation();
    }
}
