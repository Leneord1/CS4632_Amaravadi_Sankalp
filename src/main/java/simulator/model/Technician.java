package simulator.model;

import simulator.config.ServiceTimeModel;
import simulator.config.SimulationConfig;
import simulator.stochastic.ServiceTimeEquations;
import java.util.logging.Logger;

@SuppressWarnings("ALL")
public class Technician {
    private static final Logger LOGGER = Logger.getLogger(Technician.class.getName());

    private final int technicianId;
    private int experienceLevel;
    private boolean isAvailable;
    private ServiceTicket currentTicket;
    private RepairBay assignedBay;
    private final double experienceAlpha;
    private final int maxExperienceLevel;
    private final ServiceTimeModel serviceTimeModel;

    public Technician(int technicianId, int experienceLevel) {
        this(
                technicianId,
                experienceLevel,
                SimulationConfig.builder().serviceTimeModel(ServiceTimeModel.LEGACY).build());
    }

    public Technician(int technicianId, int experienceLevel, SimulationConfig config) {
        this.technicianId = technicianId;
        this.experienceLevel = experienceLevel;
        this.isAvailable = true;
        this.experienceAlpha = config.getExperienceAlpha();
        this.maxExperienceLevel = config.getMaxExperienceLevel();
        this.serviceTimeModel = config.getServiceTimeModel();
    }

    public double getServiceTime() {
        /*
        Calculate the service time based on
        the current ticket and the technician's experience level
         */
        if (currentTicket == null) {
            return 0.0;
        }

        double baseLaborTime = currentTicket.getStatus() == TicketStatus.COMPLETE
                ? currentTicket.getActualLaborTime()
                : currentTicket.getEstimatedLaborTime();

        if (serviceTimeModel == ServiceTimeModel.PDF) {
            double normalizedExperience = ServiceTimeEquations.normalizeExperienceLevel(
                    experienceLevel,
                    maxExperienceLevel);
            return ServiceTimeEquations.effectiveMeanServiceTime(
                    baseLaborTime,
                    normalizedExperience,
                    experienceAlpha);
        }

        double experienceFactor = 1.0 - (experienceLevel * 0.05);
        return baseLaborTime * Math.max(experienceFactor, 0.5);
    }

    public void printAssignment(ServiceTicket ticket) {
        // Log the assignment of a ticket to this technician
        LOGGER.info(String.format(
                "[Technician %d] start ticket #%d exp=%d serviceTime=%.2fh",
                technicianId, ticket.getTicketId(), experienceLevel, ticket.getActualLaborTime()));
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public ServiceTicket getCurrentTicket() {
        return currentTicket;
    }

    public void setCurrentTicket(ServiceTicket currentTicket) {
        this.currentTicket = currentTicket;
    }

    public RepairBay getAssignedBay() {
        return assignedBay;
    }

    public void setAssignedBay(RepairBay assignedBay) {
        this.assignedBay = assignedBay;
    }
}
