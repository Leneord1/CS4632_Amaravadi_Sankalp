package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PartTest {
    @Test
    void storesInventoryProperties() {
        Part part = new Part(7, "Oil Filter", 12, 5, 10, 1.5);

        assertEquals(7, part.getPartId());
        assertEquals("Oil Filter", part.getDescription());
        assertEquals(12, part.getQuantityOnHand());
        assertEquals(5, part.getReorderPoint());
        assertEquals(10, part.getReorderQuantity());
        assertEquals(1.5, part.getMeanLeadTimeHours(), 1e-9);
    }
}
