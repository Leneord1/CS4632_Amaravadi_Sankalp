package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnicianTest {
    @Test
    void serviceTimeScalesWithExperience() {
        Technician technician = new Technician(4, 6);
        technician.setCurrentTicket(new ServiceTicket(10, "Engine: Timing Cover Gasket R&R", 22.0));

        assertEquals(15.4, technician.getServiceTime(), 1e-9);
    }

    @Test
    void serviceTimeIsZeroWithoutAssignedTicket() {
        Technician technician = new Technician(1, 5);
        assertEquals(0.0, technician.getServiceTime());
    }

    @Test
    void serviceTimeDoesNotDropBelowHalfOfEstimate() {
        Technician technician = new Technician(2, 20);
        technician.setCurrentTicket(new ServiceTicket(11, "Transmission: A/T R&R", 6.0));

        assertEquals(3.0, technician.getServiceTime(), 1e-9);
    }

    @Test
    void bayAssignmentIsStored() {
        Technician technician = new Technician(3, 4);
        RepairBay bay = new RepairBay(12);
        technician.setAssignedBay(bay);

        assertSame(bay, technician.getAssignedBay());
    }

    @Test
    void newTechnicianStartsAvailable() {
        Technician technician = new Technician(8, 2);
        assertTrue(technician.isAvailable());
    }

    @Test
    void setAvailableUpdatesStatus() {
        Technician technician = new Technician(8, 2);
        technician.setAvailable(false);
        assertFalse(technician.isAvailable());
    }
}
