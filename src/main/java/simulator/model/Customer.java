package simulator.model;

import java.util.logging.Logger;

public class Customer {
    private static final Logger LOGGER = Logger.getLogger(Customer.class.getName());

    private final double arrivalTime;
    private double totalWaitTime;
    private ServiceAdvisor assignedAdvisor;
    private ServiceTicket ticket;

    public Customer(double arrivalTime) {
        this.arrivalTime = arrivalTime;
        this.totalWaitTime = 0.0;
    }


    public void printArrival(double currentTimeHours) {
        LOGGER.info(String.format(
                "[Customer] arrived t=%.2fh (recorded arrival=%.2fh)", currentTimeHours, arrivalTime));
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(double totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    }

    public ServiceAdvisor getAssignedAdvisor() {
        return assignedAdvisor;
    }

    public void setAssignedAdvisor(ServiceAdvisor assignedAdvisor) {
        this.assignedAdvisor = assignedAdvisor;
    }

    public ServiceTicket getTicket() {
        return ticket;
    }

    public void setTicket(ServiceTicket ticket) {
        this.ticket = ticket;
    }
}
