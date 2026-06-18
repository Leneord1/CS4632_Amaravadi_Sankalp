package simulator.model;

public class ServiceTicket {
    private int ticketId;
    private String jobType;
    private double estimatedLaborTime;
    private Technician assignedTechnician;
    private TicketStatus status;
    private double queueDelay;
    private double partsDelay;

    public ServiceTicket(int ticketId, String jobType, double estimatedLaborTime) {
        this.ticketId = ticketId;
        this.jobType = jobType;
        this.estimatedLaborTime = estimatedLaborTime;
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
}
