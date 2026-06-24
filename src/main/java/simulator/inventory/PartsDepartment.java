package simulator.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import simulator.config.SimulationConfig;
import simulator.model.Part;
import simulator.model.PartRequirement;
import simulator.model.PartsInventory;
import simulator.model.ServiceTicket;
import simulator.model.TicketStatus;

public class PartsDepartment {
    private final PartsInventory inventory;
    private final List<PendingPartOrder> pendingOrders = new ArrayList<>();
    private final List<ServiceTicket> blockedTickets = new ArrayList<>();
    private final Map<ServiceTicket, Double> blockedSinceTimeByTicket = new HashMap<>();
    private final Random random;
    private int nextOrderId = 1;
    private double totalFulfillmentTimeHours;
    private int fulfillmentCount;

    public PartsDepartment(PartsInventory inventory, Random random) {
        this.inventory = inventory;
        this.random = random;
    }

    public static PartsDepartment fromConfig(SimulationConfig config) {
        PartsInventory partsInventory = new PartsInventory();
        Part defaultPart = new Part(
                1,
                "General Repair Part",
                config.getInitialPartsQuantityOnHand(),
                config.getPartsReorderPoint(),
                config.getPartsReorderQuantity(),
                config.getPartsLeadTimeHours());
        partsInventory.addPart(defaultPart);
        return new PartsDepartment(partsInventory, new Random(config.getRandomSeed()));
    }

    public void printStatus(double currentTimeHours) {
        System.out.printf(
                "[PartsDept] t=%.2fh pendingOrders=%d blocked=%d avgFulfill=%.2fh%n",
                currentTimeHours, pendingOrders.size(), blockedTickets.size(), getAverageFulfillmentTimeHours());
    }

    public PartsFulfillmentResult requestPartsForTicket(ServiceTicket ticket, double currentTimeHours) {
        if (ticket.getRequiredParts().isEmpty()) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            return new PartsFulfillmentResult(PartsFulfillmentStatus.FULFILLED, ticket.getPartsDelay());
        }

        if (inventory.fulfillRequirements(ticket.getRequiredParts())) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            triggerReordersForRequirements(ticket.getRequiredParts(), currentTimeHours);
            return new PartsFulfillmentResult(PartsFulfillmentStatus.FULFILLED, ticket.getPartsDelay());
        }

        ticket.setStatus(TicketStatus.BLOCKED);
        if (!blockedTickets.contains(ticket)) {
            blockedTickets.add(ticket);
            blockedSinceTimeByTicket.put(ticket, currentTimeHours);
        }

        placeOrdersForShortages(ticket.getRequiredParts(), currentTimeHours);
        return new PartsFulfillmentResult(PartsFulfillmentStatus.BLOCKED, ticket.getPartsDelay());
    }

    public List<ServiceTicket> processPendingOrders(double currentTimeHours) {
        List<ServiceTicket> releasedTickets = new ArrayList<>();
        List<PendingPartOrder> dueOrders = new ArrayList<>();
        for (PendingPartOrder order : pendingOrders) {
            if (order.isDue(currentTimeHours)) {
                dueOrders.add(order);
            }
        }

        for (PendingPartOrder order : dueOrders) {
            inventory.receiveStock(order.partId(), order.quantity());
            pendingOrders.remove(order);
            releasedTickets.addAll(releaseBlockedTickets(currentTimeHours));
        }
        return releasedTickets;
    }

    public double getAverageFulfillmentTimeHours() {
        if (fulfillmentCount == 0) {
            return 0.0;
        }
        return totalFulfillmentTimeHours / fulfillmentCount;
    }

    public PartsInventory getInventory() {
        return inventory;
    }

    public List<PendingPartOrder> getPendingOrders() {
        return List.copyOf(pendingOrders);
    }

    public List<ServiceTicket> getBlockedTickets() {
        return List.copyOf(blockedTickets);
    }

    private List<ServiceTicket> releaseBlockedTickets(double currentTimeHours) {
        List<ServiceTicket> releasedTickets = new ArrayList<>();
        Iterator<ServiceTicket> iterator = blockedTickets.iterator();
        while (iterator.hasNext()) {
            ServiceTicket ticket = iterator.next();
            if (inventory.areRequirementsAvailable(ticket.getRequiredParts())) {
                continue;
            }

            inventory.fulfillRequirements(ticket.getRequiredParts());
            double blockedSince = blockedSinceTimeByTicket.getOrDefault(ticket, currentTimeHours);
            double partsDelay = currentTimeHours - blockedSince;
            ticket.setPartsDelay(ticket.getPartsDelay() + partsDelay);
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            blockedSinceTimeByTicket.remove(ticket);
            iterator.remove();
            releasedTickets.add(ticket);
            recordFulfillment(partsDelay);
            triggerReordersForRequirements(ticket.getRequiredParts(), currentTimeHours);
        }
        return releasedTickets;
    }

    private void triggerReordersForRequirements(Iterable<PartRequirement> requirements, double currentTimeHours) {
        for (PartRequirement requirement : requirements) {
            Part part = inventory.getPart(requirement.partId());
            if (part != null && inventory.shouldReorder(part)) {
                placeOrder(part, currentTimeHours);
            }
        }
    }

    private void placeOrdersForShortages(Iterable<PartRequirement> requirements, double currentTimeHours) {
        for (PartRequirement requirement : requirements) {
            if (inventory.isPartAvailable(requirement.partId(), requirement.quantity())) {
                continue;
            }

            Part part = inventory.getPart(requirement.partId());
            if (part != null && !hasOpenOrderForPart(part.getPartId())) {
                placeOrder(part, currentTimeHours);
            }
        }
    }

    private boolean hasOpenOrderForPart(int partId) {
        for (PendingPartOrder order : pendingOrders) {
            if (order.partId() == partId) {
                return true;
            }
        }
        return false;
    }

    private void placeOrder(Part part, double currentTimeHours) {
        double leadTimeHours = PartsInventoryEquations.sampleLeadTimeHours(random, part.getMeanLeadTimeHours());
        pendingOrders.add(new PendingPartOrder(
                nextOrderId++,
                part.getPartId(),
                part.getReorderQuantity(),
                currentTimeHours,
                currentTimeHours + leadTimeHours));
    }

    private void recordFulfillment(double fulfillmentTimeHours) {
        totalFulfillmentTimeHours += fulfillmentTimeHours;
        fulfillmentCount++;
    }
}
