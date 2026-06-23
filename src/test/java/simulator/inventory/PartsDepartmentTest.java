package simulator.inventory;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import simulator.config.SimulationConfig;
import simulator.model.Part;
import simulator.model.PartsInventory;
import simulator.model.ServiceTicket;
import simulator.model.TicketStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartsDepartmentTest {
    @Test
    void fulfillsTicketWhenPartsAreInStock() {
        PartsInventory inventory = new PartsInventory();
        inventory.addPart(new Part(1, "Filter", 10, 5, 10, 2.0));
        PartsDepartment department = new PartsDepartment(inventory, new Random(1));

        ServiceTicket ticket = new ServiceTicket(1, "Oil Change", 0.5, 0.5);
        ticket.addRequiredPart(1, 1);

        PartsFulfillmentResult result = department.requestPartsForTicket(ticket, 1.0);

        assertTrue(result.isFulfilled());
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        assertEquals(9, inventory.getPart(1).getQuantityOnHand());
    }

    @Test
    void blocksTicketAndCreatesPendingOrderWhenStockIsUnavailable() {
        PartsInventory inventory = new PartsInventory();
        inventory.addPart(new Part(1, "Sensor", 0, 5, 10, 2.0));
        PartsDepartment department = new PartsDepartment(inventory, new Random(2));

        ServiceTicket ticket = new ServiceTicket(2, "Diagnostics", 1.0, 1.0);
        ticket.addRequiredPart(1, 1);

        PartsFulfillmentResult result = department.requestPartsForTicket(ticket, 2.0);

        assertEquals(PartsFulfillmentStatus.BLOCKED, result.getStatus());
        assertEquals(TicketStatus.BLOCKED, ticket.getStatus());
        assertEquals(1, department.getPendingOrders().size());
        assertEquals(1, department.getBlockedTickets().size());
    }

    @Test
    void releasesBlockedTicketAfterOrderArrives() {
        PartsInventory inventory = new PartsInventory();
        inventory.addPart(new Part(1, "Gasket", 0, 5, 10, 1.0));
        PartsDepartment department = new PartsDepartment(inventory, new Random(3));

        ServiceTicket ticket = new ServiceTicket(3, "Engine Repair", 2.0, 2.0);
        ticket.addRequiredPart(1, 1);
        department.requestPartsForTicket(ticket, 1.0);

        PendingPartOrder order = department.getPendingOrders().get(0);
        List<ServiceTicket> released = department.processPendingOrders(order.getArrivalTimeHours());

        assertEquals(1, released.size());
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
        assertTrue(ticket.getPartsDelay() > 0.0);
        assertTrue(department.getAverageFulfillmentTimeHours() > 0.0);
        assertFalse(department.getBlockedTickets().contains(ticket));
    }

    @Test
    void buildsFromSimulationConfig() {
        SimulationConfig config = SimulationConfig.builder()
                .initialPartsQuantityOnHand(15)
                .partsReorderPoint(4)
                .partsReorderQuantity(8)
                .partsLeadTimeHours(1.5)
                .randomSeed(9L)
                .build();

        PartsDepartment department = PartsDepartment.fromConfig(config);

        assertEquals(15, department.getInventory().getPart(1).getQuantityOnHand());
        assertEquals(4, department.getInventory().getPart(1).getReorderPoint());
    }
}
