package simulator.model;

public class Customer {
    private double arrivalTime;
    private double totalWaitTime;
    private ServiceAdvisor assignedAdvisor;
    private ServiceTicket ticket;

    public Customer(double arrivalTime) {
        this.arrivalTime = arrivalTime;
        this.totalWaitTime = 0.0;
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
