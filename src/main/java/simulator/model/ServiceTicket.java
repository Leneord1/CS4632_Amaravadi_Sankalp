package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceTicket {
    private final int ticketId;
    private final String jobType;
    private final double estimatedLaborTime;
    private final double actualLaborTime;
    private final List<PartRequirement> requiredParts = new ArrayList<>();
    private Technician assignedTechnician;
    private TicketStatus status;
    private double queueDelay;
    private double partsDelay;

    public ServiceTicket(int ticketId, String jobType, double estimatedLaborTime, double actualLaborTime) {
        this.ticketId = ticketId;
        this.jobType = jobType;
        this.estimatedLaborTime = estimatedLaborTime;
        this.actualLaborTime = actualLaborTime;
        this.status = TicketStatus.WAITING;
        this.queueDelay = 0.0;
        this.partsDelay = 0.0;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getJobType() {
        return jobType;
    }

    public double getEstimatedLaborTime() {
        return estimatedLaborTime;
    }

    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(Technician assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public double getQueueDelay() {
        return queueDelay;
    }

    public void setQueueDelay(double queueDelay) {
        this.queueDelay = queueDelay;
    }

    public double getPartsDelay() {
        return partsDelay;
    }

    public void setPartsDelay(double partsDelay) {
        this.partsDelay = partsDelay;
    }

    public double getActualLaborTime() {
        return actualLaborTime;
    }

    public double getLaborTimeVariance() {
        return actualLaborTime - estimatedLaborTime;
    }

    public void addRequiredPart(int partId, int quantity) {
        requiredParts.add(new PartRequirement(partId, quantity));
    }

    public List<PartRequirement> getRequiredParts() {
        return Collections.unmodifiableList(requiredParts);
    }
}
