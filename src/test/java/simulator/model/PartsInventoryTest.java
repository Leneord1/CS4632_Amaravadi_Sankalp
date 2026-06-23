package simulator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartsInventoryTest {
    private PartsInventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new PartsInventory();
        inventory.addPart(new Part(1, "Brake Pad", 10, 5, 10, 2.0));
    }

    @Test
    void requestPartReducesOnHandQuantity() {
        assertTrue(inventory.requestPart(1, 3));
        assertEquals(7, inventory.getPart(1).getQuantityOnHand());
    }

    @Test
    void requestPartFailsWhenStockIsInsufficient() {
        assertFalse(inventory.requestPart(1, 12));
        assertEquals(10, inventory.getPart(1).getQuantityOnHand());
    }

    @Test
    void fulfillRequirementsDeductsAllRequestedParts() {
        ServiceTicket ticket = new ServiceTicket(1, "Brakes", 1.0, 1.0);
        ticket.addRequiredPart(1, 2);
        ticket.addRequiredPart(1, 1);

        assertTrue(inventory.fulfillRequirements(ticket.getRequiredParts()));
        assertEquals(7, inventory.getPart(1).getQuantityOnHand());
    }

    @Test
    void shouldReorderWhenBelowReorderPoint() {
        inventory.getPart(1).setQuantityOnHand(4);
        assertTrue(inventory.shouldReorder(inventory.getPart(1)));
    }
}
