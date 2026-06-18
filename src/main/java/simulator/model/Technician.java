package simulator.model;

public class Technician {
    private int technicianId;
    private int experienceLevel;
    private boolean isAvailable;
    private ServiceTicket currentTicket;
    private RepairBay assignedBay;

    public Technician(int technicianId, int experienceLevel) {
        this.technicianId = technicianId;
        this.experienceLevel = experienceLevel;
        this.isAvailable = true;
    }

    public double getServiceTime() {
        if (currentTicket == null) {
            return 0.0;
        }

        double experienceFactor = 1.0 - (experienceLevel * 0.05);
        return currentTicket.getEstimatedLaborTime() * Math.max(experienceFactor, 0.5);
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
