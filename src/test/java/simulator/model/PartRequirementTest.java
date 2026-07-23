package simulator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PartRequirementTest {
    @Test
    void storesPartIdAndQuantity() {
        PartRequirement requirement = new PartRequirement(3, 2);
        assertEquals(3, requirement.partId());
        assertEquals(2, requirement.quantity());
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new PartRequirement(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new PartRequirement(1, -1));
    }
}
