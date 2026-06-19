package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceAdvisorTest {
    @Test
    void intakeCustomerCreatesTicketAndLinksEntities() {
        ServiceAdvisor advisor = new ServiceAdvisor(7);
        Customer customer = new Customer(9.5);

        ServiceTicket ticket = advisor.intakeCustomer(customer, 101, "Diagnostics", 1.0);

        assertEquals(101, ticket.getTicketId());
        assertEquals("Diagnostics", ticket.getJobType());
        assertSame(advisor, customer.getAssignedAdvisor());
        assertSame(ticket, customer.getTicket());
        assertSame(customer, advisor.getCurrentCustomer());
        assertFalse(advisor.isAvailable());
    }

    @Test
    void setAvailableRestoresAdvisorState() {
        ServiceAdvisor advisor = new ServiceAdvisor(1);
        advisor.intakeCustomer(new Customer(1.0), 1, "Oil Change", 0.3);
        advisor.setAvailable(true);
        assertTrue(advisor.isAvailable());
    }
}
