package simulator.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PartsInventoryEquationsTest {
    @Test
    void reorderTriggersBelowReorderPoint() {
        assertTrue(PartsInventoryEquations.shouldReorder(4, 5));
    }

    @Test
    void reorderDoesNotTriggerAtReorderPoint() {
        assertFalse(PartsInventoryEquations.shouldReorder(5, 5));
    }

    @Test
    void inventoryAfterReorderAddsQuantity() {
        assertEquals(15, PartsInventoryEquations.inventoryAfterReorder(5, 10));
    }

    @Test
    void partAvailabilityChecksRequiredQuantity() {
        assertTrue(PartsInventoryEquations.isPartAvailable(3, 2));
        assertFalse(PartsInventoryEquations.isPartAvailable(1, 2));
    }

    @Test
    void fulfillmentReducesInventory() {
        assertEquals(2, PartsInventoryEquations.inventoryAfterFulfillment(5, 3));
    }

    @Test
    void averageLeadTimeUsesObservedSamples() {
        assertEquals(
                7.0 / 3.0,
                PartsInventoryEquations.averageLeadTimeHours(new double[] {1.0, 2.0, 4.0}),
                1e-9);
    }

    @Test
    void rejectsInsufficientInventoryForFulfillment() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PartsInventoryEquations.inventoryAfterFulfillment(1, 2));
    }
}
