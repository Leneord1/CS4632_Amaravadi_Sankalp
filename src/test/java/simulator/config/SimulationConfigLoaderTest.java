package simulator.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SimulationConfigLoaderTest {
    @Test
    void loadsDefaultsFromPropertiesFile() {
        SimulationConfig config = SimulationConfigLoader.loadDefault();

        assertEquals(10.0, config.getSimulationHorizonHours(), 1e-9);
        assertEquals(4.0, config.getArrivalRate(), 1e-9);
        assertEquals(3, config.getTechnicianCount());
        assertEquals(2.0, config.getServiceRatePerTechnician(), 1e-9);
        assertEquals(1.0, config.getExperienceAlpha(), 1e-9);
        assertEquals(4.0, config.getGammaShapeParameter(), 1e-9);
        assertEquals(0.15, config.getValidationRelativeTolerance(), 1e-9);
        assertEquals(ServiceTimeModel.PDF, config.getServiceTimeModel());
    }

    @Test
    void cliOverridesProperties() {
        SimulationConfig config =
                SimulationConfigLoader.load(
                        new String[] {
                            "--arrival-rate=6",
                            "--technicians=5",
                            "--validation-tolerance=0.2",
                            "--service-time-model=LEGACY"
                        });

        assertEquals(6.0, config.getArrivalRate(), 1e-9);
        assertEquals(5, config.getTechnicianCount());
        assertEquals(0.2, config.getValidationRelativeTolerance(), 1e-9);
        assertEquals(ServiceTimeModel.LEGACY, config.getServiceTimeModel());
    }

    @Test
    void booleanFlagsDoNotBreakOptionParsing() {
        SimulationConfig config =
                SimulationConfigLoader.load(
                        new String[] {"--no-prompt", "--replications=5", "--horizon=3"});
        assertEquals(5, config.getReplicationCount());
        assertEquals(3.0, config.getSimulationHorizonHours(), 1e-9);
    }

    @Test
    void meanServiceTimeUsesMu() {
        SimulationConfig config = SimulationConfig.builder().serviceRatePerTechnician(2.0).build();
        assertEquals(0.5, config.getMeanServiceTimeHours(), 1e-9);
    }
}
