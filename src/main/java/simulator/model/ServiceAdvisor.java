package simulator.model;

import java.util.logging.Logger;

public class ServiceAdvisor {
    private static final Logger LOGGER = Logger.getLogger(ServiceAdvisor.class.getName());

    private final int advisorId;
    private boolean isAvailable;
    private Customer currentCustomer;

    public ServiceAdvisor(int advisorId) {
        this.advisorId = advisorId;
        this.isAvailable = true;
    }

    public ServiceTicket intakeCustomer(
            Customer customer,
            int ticketId,
            String jobType,
            double estimatedLaborTime,
            double actualLaborTime) {
        //  Intake a new customer and assign them to this advisor
        currentCustomer = customer;
        isAvailable = false;
        customer.setAssignedAdvisor(this);

        ServiceTicket ticket =
                new ServiceTicket(ticketId, jobType, estimatedLaborTime, actualLaborTime);
        customer.setTicket(ticket);
        return ticket;
    }

    public void printIntake(ServiceTicket ticket) {
        LOGGER.info(
                String.format(
                        "[Advisor %d] intake ticket #%d (%s)",
                        advisorId, ticket.getTicketId(), ticket.getJobType()));
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
