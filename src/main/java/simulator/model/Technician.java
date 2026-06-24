package simulator.model;

import simulator.config.ServiceTimeModel;
import simulator.config.SimulationConfig;
import simulator.stochastic.ServiceTimeEquations;

@SuppressWarnings("ALL")
public class Technician {
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
        System.out.printf(
                "[Technician %d] start ticket #%d exp=%d serviceTime=%.2fh%n",
                technicianId, ticket.getTicketId(), experienceLevel, ticket.getActualLaborTime());
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
