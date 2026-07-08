package simulator.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class SimulationConfigLoader {
    private static final String DEFAULT_PROPERTIES_RESOURCE = "simulation.properties";
    private static final String CONFIG_ARG_PREFIX = "--config=";

    private SimulationConfigLoader() {}

    public static SimulationConfig loadDefault() {
        return load(new String[0]);
    }

    public static SimulationConfig load(String[] args) {
        if (containsHelp(args)) {
            throw new IllegalArgumentException(usage());
        }

        Properties properties = loadProperties(args);
        SimulationConfig.Builder builder = SimulationConfig.builder();
        applyProperties(builder, properties);
        applyCliOverrides(builder, args);
        return builder.build();
    }

    public static String usage() {
        return String.join(
                System.lineSeparator(),
                "Usage: java -jar service-department-simulator.jar [options]",
                "  --config=<path>                  Optional properties file override",
                "  --horizon=<hours>                Simulation run length",
                "  --arrival-rate=<lambda>          Customer arrival rate (vehicles/hour)",
                "  --arrival-profile=<CONSTANT|DEALERSHIP_DAY>",
                "  --technicians=<count>            Technician and bay count",
                "  --service-rate=<mu>              Service rate per technician",
                "  --advisors=<count>               Service advisor count",
                "  --experience-alpha=<alpha>       Experience coefficient (Eq. 6)",
                "  --max-experience=<level>         Max experience level for normalization",
                "  --gamma-shape=<k>                Gamma shape parameter (Eq. 8)",
                "  --service-time-model=<LEGACY|PDF>",
                "  --reorder-point=<r>              Parts reorder point",
                "  --reorder-quantity=<Q>           Parts reorder quantity",
                "  --parts-lead-time=<hours>        Average parts lead time",
                "  --initial-parts-qty=<count>      Starting on-hand parts quantity",
                "  --validation-tolerance=<ratio>   Analytical validation tolerance",
                "  --replications=<count>           Number of simulation replications",
                "  --seed=<value>                   Random seed",
                "  --help                           Show this message");
    }

    private static boolean containsHelp(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static Properties loadProperties(String[] args) {
        Properties properties = new Properties();
        loadResourceProperties(properties);

        String configPath = readConfigPath(args);
        if (configPath != null) {
            loadFileProperties(properties, Path.of(configPath));
        }
        return properties;
    }

    private static void loadResourceProperties(Properties properties) {
        try (InputStream inputStream =
                SimulationConfigLoader.class
                        .getClassLoader()
                        .getResourceAsStream(SimulationConfigLoader.DEFAULT_PROPERTIES_RESOURCE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to load " + SimulationConfigLoader.DEFAULT_PROPERTIES_RESOURCE,
                    exception);
        }
    }

    private static void loadFileProperties(Properties properties, Path configPath) {
        if (!Files.exists(configPath)) {
            throw new IllegalArgumentException("Config file not found: " + configPath);
        }

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load config file: " + configPath, exception);
        }
    }

    private static String readConfigPath(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(CONFIG_ARG_PREFIX)) {
                return arg.substring(CONFIG_ARG_PREFIX.length());
            }
        }
        return null;
    }

    private static void applyProperties(SimulationConfig.Builder builder, Properties properties) {
        applyDoubleProperty(properties, "simulation.horizonHours", builder::simulationHorizonHours);
        applyDoubleProperty(properties, "simulation.arrivalRate", builder::arrivalRate);
        applyIntProperty(properties, "simulation.technicianCount", builder::technicianCount);
        applyDoubleProperty(
                properties,
                "simulation.serviceRatePerTechnician",
                builder::serviceRatePerTechnician);
        applyIntProperty(properties, "simulation.advisorCount", builder::advisorCount);
        applyDoubleProperty(properties, "serviceTime.experienceAlpha", builder::experienceAlpha);
        applyIntProperty(properties, "serviceTime.maxExperienceLevel", builder::maxExperienceLevel);
        applyDoubleProperty(
                properties, "serviceTime.gammaShapeParameter", builder::gammaShapeParameter);
        applyEnumProperty(properties, builder::serviceTimeModel);
        applyArrivalProfileProperty(properties, builder::arrivalProfile);
        applyIntProperty(properties, "parts.reorderPoint", builder::partsReorderPoint);
        applyIntProperty(properties, "parts.reorderQuantity", builder::partsReorderQuantity);
        applyDoubleProperty(properties, "parts.leadTimeHours", builder::partsLeadTimeHours);
        applyIntProperty(
                properties, "parts.initialQuantityOnHand", builder::initialPartsQuantityOnHand);
        applyDoubleProperty(
                properties, "validation.relativeTolerance", builder::validationRelativeTolerance);
        applyIntProperty(properties, "simulation.replicationCount", builder::replicationCount);
        applyLongProperty(properties, builder::randomSeed);
    }

    private static void applyCliOverrides(SimulationConfig.Builder builder, String[] args) {
        Map<String, String> options = parseOptions(args);
        applyCliDouble(options, "horizon", builder::simulationHorizonHours);
        applyCliDouble(options, "arrival-rate", builder::arrivalRate);
        applyCliInt(options, "technicians", builder::technicianCount);
        applyCliDouble(options, "service-rate", builder::serviceRatePerTechnician);
        applyCliInt(options, "advisors", builder::advisorCount);
        applyCliDouble(options, "experience-alpha", builder::experienceAlpha);
        applyCliInt(options, "max-experience", builder::maxExperienceLevel);
        applyCliDouble(options, "gamma-shape", builder::gammaShapeParameter);
        applyCliEnum(options, builder::serviceTimeModel);
        applyCliArrivalProfile(options, builder::arrivalProfile);
        applyCliInt(options, "reorder-point", builder::partsReorderPoint);
        applyCliInt(options, "reorder-quantity", builder::partsReorderQuantity);
        applyCliDouble(options, "parts-lead-time", builder::partsLeadTimeHours);
        applyCliInt(options, "initial-parts-qty", builder::initialPartsQuantityOnHand);
        applyCliDouble(options, "validation-tolerance", builder::validationRelativeTolerance);
        applyCliInt(options, "replications", builder::replicationCount);
        applyCliLong(options, builder::randomSeed);
    }

    private static Map<String, String> parseOptions(String[] args) {
        Map<String, String> options = new HashMap<>();
        for (String arg : args) {
            if (!arg.startsWith("--")
                    || arg.equals("--help")
                    || arg.startsWith(CONFIG_ARG_PREFIX)) {
                continue;
            }

            int separatorIndex = arg.indexOf('=');
            if (separatorIndex <= 2) {
                throw new IllegalArgumentException("Invalid option format: " + arg);
            }

            String key = arg.substring(2, separatorIndex);
            String value = arg.substring(separatorIndex + 1);
            options.put(key, value);
        }
        return options;
    }

    private static void applyDoubleProperty(
            Properties properties, String key, DoubleConsumer setter) {
        String value = properties.getProperty(key);
        if (value != null) {
            setter.accept(Double.parseDouble(value.trim()));
        }
    }

    private static void applyIntProperty(Properties properties, String key, IntConsumer setter) {
        String value = properties.getProperty(key);
        if (value != null) {
            setter.accept(Integer.parseInt(value.trim()));
        }
    }

    private static void applyLongProperty(Properties properties, LongConsumer setter) {
        String value = properties.getProperty("simulation.randomSeed");
        if (value != null) {
            setter.accept(Long.parseLong(value.trim()));
        }
    }

    private static void applyEnumProperty(
            Properties properties, Consumer<ServiceTimeModel> setter) {
        String value = properties.getProperty("serviceTime.model");
        if (value != null) {
            setter.accept(ServiceTimeModel.valueOf(value.trim().toUpperCase()));
        }
    }

    private static void applyArrivalProfileProperty(
            Properties properties, Consumer<ArrivalProfile> setter) {
        String value = properties.getProperty("simulation.arrivalProfile");
        if (value != null) {
            setter.accept(ArrivalProfile.valueOf(value.trim().toUpperCase()));
        }
    }

    private static void applyCliDouble(
            Map<String, String> options, String key, DoubleConsumer setter) {
        String value = options.get(key);
        if (value != null) {
            setter.accept(Double.parseDouble(value));
        }
    }

    private static void applyCliInt(Map<String, String> options, String key, IntConsumer setter) {
        String value = options.get(key);
        if (value != null) {
            setter.accept(Integer.parseInt(value));
        }
    }

    private static void applyCliLong(Map<String, String> options, LongConsumer setter) {
        String value = options.get("seed");
        if (value != null) {
            setter.accept(Long.parseLong(value));
        }
    }

    private static void applyCliEnum(
            Map<String, String> options, Consumer<ServiceTimeModel> setter) {
        String value = options.get("service-time-model");
        if (value != null) {
            setter.accept(ServiceTimeModel.valueOf(value.trim().toUpperCase()));
        }
    }

    private static void applyCliArrivalProfile(
            Map<String, String> options, Consumer<ArrivalProfile> setter) {
        String value = options.get("arrival-profile");
        if (value != null) {
            setter.accept(ArrivalProfile.valueOf(value.trim().toUpperCase()));
        }
    }
}
