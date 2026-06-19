package simulator.model;

public class ServiceAdvisor {
    private final int advisorId;
    private boolean isAvailable;
    private Customer currentCustomer;

    public ServiceAdvisor(int advisorId) {
        this.advisorId = advisorId;
        this.isAvailable = true;
    }

    public ServiceTicket intakeCustomer(Customer customer, int ticketId, String jobType, double estimatedLaborTime, double actualLaborTime) {
        currentCustomer = customer;
        isAvailable = false;
        customer.setAssignedAdvisor(this);

        ServiceTicket ticket = new ServiceTicket(ticketId, jobType, estimatedLaborTime, actualLaborTime);
        customer.setTicket(ticket);
        return ticket;
    }

    public int getAdvisorId() {
        return advisorId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }
}
