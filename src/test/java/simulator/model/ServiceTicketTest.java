package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServiceTicketTest {
    @Test
    void newTicketStartsWaitingWithZeroDelays() {
        ServiceTicket ticket = new ServiceTicket(5, "Electrical: Battery change", 0.5, 0.1);

        assertEquals(TicketStatus.WAITING, ticket.getStatus());
        assertEquals(0.1, ticket.getActualLaborTime());
        assertEquals(-0.4, ticket.getLaborTimeVariance(), 1e-9);
        assertEquals(0.0, ticket.getQueueDelay());
        assertEquals(0.0, ticket.getPartsDelay());
    }

    @Test
    void statusAndDelaysCanBeUpdated() {
        ServiceTicket ticket = new ServiceTicket(6, "AC: AC Condenser Change", 3.0, 2.89);
        ticket.setStatus(TicketStatus.BLOCKED);
        ticket.setQueueDelay(1.5);
        ticket.setPartsDelay(2.0);

        assertEquals(TicketStatus.BLOCKED, ticket.getStatus());
        assertEquals(1.5, ticket.getQueueDelay());
        assertEquals(2.0, ticket.getPartsDelay());
    }

    @Test
    void technicianAssignmentIsStored() {
        ServiceTicket ticket = new ServiceTicket(7, "Suspension: Control Arm Replacement", 2.0, 1.0);
        Technician technician = new Technician(9, 3);
        ticket.setAssignedTechnician(technician);

        assertSame(technician, ticket.getAssignedTechnician());
    }
}
