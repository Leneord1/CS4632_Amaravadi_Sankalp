package simulator.model;

public class RepairBay {
    private final int bayId;
    private boolean isOccupied;
    private Technician assignedTechnician;

    public RepairBay(int bayId) {
        this.bayId = bayId;
        this.isOccupied = false;
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
