package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepairBayTest {
    @Test
    void newBayStartsUnoccupied() {
        RepairBay bay = new RepairBay(4);
        assertEquals(4, bay.getBayId());
        assertFalse(bay.isOccupied());
    }

    @Test
    void occupancyAndTechnicianCanBeUpdated() {
        RepairBay bay = new RepairBay(1);
        Technician technician = new Technician(5, 4);
        bay.setOccupied(true);
        bay.setAssignedTechnician(technician);

        assertTrue(bay.isOccupied());
        assertSame(technician, bay.getAssignedTechnician());
    }
}
