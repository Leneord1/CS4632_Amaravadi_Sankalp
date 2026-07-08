package simulator.config;

import java.util.logging.Logger;

public final class SimulationConfig {
    private static final Logger LOGGER = Logger.getLogger(SimulationConfig.class.getName());
    private final double simulationHorizonHours;
    private final double arrivalRate;
    private final int technicianCount;
    private final double serviceRatePerTechnician;
    private final int advisorCount;
    private final int customerCount;
    private final double experienceAlpha;
    private final int maxExperienceLevel;
    private final double gammaShapeParameter;
    private final int partsReorderPoint;
    private final int partsReorderQuantity;
    private final double partsLeadTimeHours;
    private final int initialPartsQuantityOnHand;
    private final double validationRelativeTolerance;
    private final int replicationCount;
    private final long randomSeed;
    private final ServiceTimeModel serviceTimeModel;
    private final ArrivalProfile arrivalProfile;

    private SimulationConfig(Builder builder) {
        this.simulationHorizonHours = builder.simulationHorizonHours;
        this.arrivalRate = builder.arrivalRate;
        this.technicianCount = builder.technicianCount;
        this.serviceRatePerTechnician = builder.serviceRatePerTechnician;
        this.advisorCount = builder.advisorCount;
        this.customerCount = builder.customerCount;
        this.experienceAlpha = builder.experienceAlpha;
        this.maxExperienceLevel = builder.maxExperienceLevel;
        this.gammaShapeParameter = builder.gammaShapeParameter;
        this.partsReorderPoint = builder.partsReorderPoint;
        this.partsReorderQuantity = builder.partsReorderQuantity;
        this.partsLeadTimeHours = builder.partsLeadTimeHours;
        this.initialPartsQuantityOnHand = builder.initialPartsQuantityOnHand;
        this.validationRelativeTolerance = builder.validationRelativeTolerance;
        this.replicationCount = builder.replicationCount;
        this.randomSeed = builder.randomSeed;
        this.serviceTimeModel = builder.serviceTimeModel;
        this.arrivalProfile = builder.arrivalProfile;
    }

    public static SimulationConfig defaults() {
        return builder().build();
    }

    public void printConfig(String label) {
        LOGGER.info(() -> String.format(
                "[Config] %s horizon=%.1fh lambda=%.2f arrival=%s techs=%d mu=%.2f advisors=%d model=%s",
                label, simulationHorizonHours, arrivalRate, arrivalProfile, technicianCount,
                serviceRatePerTechnician, advisorCount, serviceTimeModel));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .simulationHorizonHours(simulationHorizonHours)
                .arrivalRate(arrivalRate)
                .technicianCount(technicianCount)
                .serviceRatePerTechnician(serviceRatePerTechnician)
                .advisorCount(advisorCount)
                .customerCount(customerCount)
                .experienceAlpha(experienceAlpha)
                .maxExperienceLevel(maxExperienceLevel)
                .gammaShapeParameter(gammaShapeParameter)
                .partsReorderPoint(partsReorderPoint)
                .partsReorderQuantity(partsReorderQuantity)
                .partsLeadTimeHours(partsLeadTimeHours)
                .initialPartsQuantityOnHand(initialPartsQuantityOnHand)
                .validationRelativeTolerance(validationRelativeTolerance)
                .replicationCount(replicationCount)
                .randomSeed(randomSeed)
                .serviceTimeModel(serviceTimeModel)
                .arrivalProfile(arrivalProfile);
    }

    public double getSimulationHorizonHours() {
        return simulationHorizonHours;
    }

    public double getArrivalRate() {
        return arrivalRate;
    }

    public int getTechnicianCount() {
        return technicianCount;
    }

    public double getServiceRatePerTechnician() {
        return serviceRatePerTechnician;
    }

    public int getAdvisorCount() {
        return advisorCount;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public double getExperienceAlpha() {
        return experienceAlpha;
    }

    public int getMaxExperienceLevel() {
        return maxExperienceLevel;
    }

    public double getGammaShapeParameter() {
        return gammaShapeParameter;
    }

    public int getPartsReorderPoint() {
        return partsReorderPoint;
    }

    public int getPartsReorderQuantity() {
        return partsReorderQuantity;
    }

    public double getPartsLeadTimeHours() {
        return partsLeadTimeHours;
    }

    public int getInitialPartsQuantityOnHand() {
        return initialPartsQuantityOnHand;
    }

    public double getValidationRelativeTolerance() {
        return validationRelativeTolerance;
    }

    public int getReplicationCount() {
        return replicationCount;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public ServiceTimeModel getServiceTimeModel() {
        return serviceTimeModel;
    }

    public ArrivalProfile getArrivalProfile() {
        return arrivalProfile;
    }

    public double getMeanServiceTimeHours() {
        if (serviceRatePerTechnician <= 0.0) {
            return 0.0;
        }
        return 1.0 / serviceRatePerTechnician;
    }

    public static final class Builder {
        private double simulationHorizonHours = 10.0;
        private double arrivalRate = 4.0;
        private int technicianCount = 3;
        private double serviceRatePerTechnician = 2.0;
        private int advisorCount = 4;
        private int customerCount = 0;
        private double experienceAlpha = 1.0;
        private int maxExperienceLevel = 10;
        private double gammaShapeParameter = 4.0;
        private int partsReorderPoint = 5;
        private int partsReorderQuantity = 10;
        private double partsLeadTimeHours = 2.0;
        private int initialPartsQuantityOnHand = 20;
        private double validationRelativeTolerance = 0.15;
        private int replicationCount = 1;
        private long randomSeed = 42L;
        private ServiceTimeModel serviceTimeModel = ServiceTimeModel.PDF;
        private ArrivalProfile arrivalProfile = ArrivalProfile.DEALERSHIP_DAY;

        public Builder simulationHorizonHours(double simulationHorizonHours) {
            this.simulationHorizonHours = simulationHorizonHours;
            return this;
        }

        public Builder arrivalRate(double arrivalRate) {
            this.arrivalRate = arrivalRate;
            return this;
        }

        public Builder technicianCount(int technicianCount) {
            this.technicianCount = technicianCount;
            return this;
        }

        public Builder serviceRatePerTechnician(double serviceRatePerTechnician) {
            this.serviceRatePerTechnician = serviceRatePerTechnician;
            return this;
        }

        public Builder advisorCount(int advisorCount) {
            this.advisorCount = advisorCount;
            return this;
        }

        public Builder customerCount(int customerCount) {
            this.customerCount = customerCount;
            return this;
        }

        public Builder experienceAlpha(double experienceAlpha) {
            this.experienceAlpha = experienceAlpha;
            return this;
        }

        public Builder maxExperienceLevel(int maxExperienceLevel) {
            this.maxExperienceLevel = maxExperienceLevel;
            return this;
        }

        public Builder gammaShapeParameter(double gammaShapeParameter) {
            this.gammaShapeParameter = gammaShapeParameter;
            return this;
        }

        public Builder partsReorderPoint(int partsReorderPoint) {
            this.partsReorderPoint = partsReorderPoint;
            return this;
        }

        public Builder partsReorderQuantity(int partsReorderQuantity) {
            this.partsReorderQuantity = partsReorderQuantity;
            return this;
        }

        public Builder partsLeadTimeHours(double partsLeadTimeHours) {
            this.partsLeadTimeHours = partsLeadTimeHours;
            return this;
        }

        public Builder initialPartsQuantityOnHand(int initialPartsQuantityOnHand) {
            this.initialPartsQuantityOnHand = initialPartsQuantityOnHand;
            return this;
        }

        public Builder validationRelativeTolerance(double validationRelativeTolerance) {
            this.validationRelativeTolerance = validationRelativeTolerance;
            return this;
        }

        public Builder replicationCount(int replicationCount) {
            this.replicationCount = replicationCount;
            return this;
        }

        public Builder randomSeed(long randomSeed) {
            this.randomSeed = randomSeed;
            return this;
        }

        public Builder serviceTimeModel(ServiceTimeModel serviceTimeModel) {
            this.serviceTimeModel = serviceTimeModel;
            return this;
        }

        public Builder arrivalProfile(ArrivalProfile arrivalProfile) {
            this.arrivalProfile = arrivalProfile;
            return this;
        }

        public SimulationConfig build() {
            validate();
            return new SimulationConfig(this);
        }

        private void validate() {
            if (simulationHorizonHours <= 0.0) {
                throw new IllegalArgumentException("simulationHorizonHours must be positive");
            }
            if (arrivalRate < 0.0) {
                throw new IllegalArgumentException("arrivalRate must be non-negative");
            }
            if (technicianCount <= 0) {
                throw new IllegalArgumentException("technicianCount must be positive");
            }
            if (serviceRatePerTechnician <= 0.0) {
                throw new IllegalArgumentException("serviceRatePerTechnician must be positive");
            }
            if (advisorCount <= 0) {
                throw new IllegalArgumentException("advisorCount must be positive");
            }
            if (customerCount < 0) {
                throw new IllegalArgumentException("customerCount must be non-negative");
            }
            if (experienceAlpha < 0.0) {
                throw new IllegalArgumentException("experienceAlpha must be non-negative");
            }
            if (maxExperienceLevel <= 0) {
                throw new IllegalArgumentException("maxExperienceLevel must be positive");
            }
            if (gammaShapeParameter <= 0.0) {
                throw new IllegalArgumentException("gammaShapeParameter must be positive");
            }
            if (partsReorderPoint < 0 || partsReorderQuantity < 0 || initialPartsQuantityOnHand < 0) {
                throw new IllegalArgumentException("parts inventory values must be non-negative");
            }
            if (partsLeadTimeHours < 0.0) {
                throw new IllegalArgumentException("partsLeadTimeHours must be non-negative");
            }
            if (validationRelativeTolerance < 0.0) {
                throw new IllegalArgumentException("validationRelativeTolerance must be non-negative");
            }
            if (replicationCount <= 0) {
                throw new IllegalArgumentException("replicationCount must be positive");
            }
        }
    }
}
