package simulator.model;

import java.util.logging.Logger;

public class RepairBay {
    private static final Logger LOGGER = Logger.getLogger(RepairBay.class.getName());

    private final int bayId;
    private boolean isOccupied;
    private Technician assignedTechnician;

    public RepairBay(int bayId) {
        this.bayId = bayId;
        this.isOccupied = false;
    }

    public void printOccupancy(double currentTimeHours) {
        LOGGER.info(String.format("[Bay %d] occupied=%b at t=%.2fh", bayId, isOccupied, currentTimeHours));
    }

    public int getBayId() {
        return bayId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(Technician assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }
}
