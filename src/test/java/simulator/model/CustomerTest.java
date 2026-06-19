package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class CustomerTest {
    @Test
    void constructorStoresArrivalTimeAndZeroWait() {
        Customer customer = new Customer(3.25);
        assertEquals(3.25, customer.getArrivalTime());
        assertEquals(0.0, customer.getTotalWaitTime());
    }

    @Test
    void waitTimeCanBeUpdated() {
        Customer customer = new Customer(1.0);
        customer.setTotalWaitTime(4.5);
        assertEquals(4.5, customer.getTotalWaitTime());
    }

    @Test
    void advisorAndTicketReferencesAreStored() {
        Customer customer = new Customer(2.0);
        ServiceAdvisor advisor = new ServiceAdvisor(2);
        ServiceTicket ticket = new ServiceTicket(20, "Inspection", 1.0);
        customer.setAssignedAdvisor(advisor);
        customer.setTicket(ticket);

        assertSame(advisor, customer.getAssignedAdvisor());
        assertSame(ticket, customer.getTicket());
    }
}
