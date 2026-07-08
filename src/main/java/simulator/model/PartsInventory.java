package simulator.model;

import simulator.inventory.PartsInventoryEquations;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PartsInventory {
    private static final Logger LOGGER = Logger.getLogger(PartsInventory.class.getName());

    private final Map<Integer, Part> partsById = new LinkedHashMap<>();

    public void printStockLevel(int partId) {
        Part part = partsById.get(partId);
        int quantity = part == null ? 0 : part.getQuantityOnHand();
        LOGGER.info(String.format("[Inventory] part %d on-hand=%d", partId, quantity));
    }

    public void addPart(Part part) {
        partsById.put(part.getPartId(), part);
    }

    public Part getPart(int partId) {
        return partsById.get(partId);
    }

    public Collection<Part> getParts() {
        return Collections.unmodifiableCollection(partsById.values());
    }

    public boolean requestPart(int partId, int quantity) {
        Part part = partsById.get(partId);
        if (part == null) {
            return false;
        }

        if (!PartsInventoryEquations.isPartAvailable(part.getQuantityOnHand(), quantity)) {
            return false;
        }

        part.setQuantityOnHand(
                PartsInventoryEquations.inventoryAfterFulfillment(
                        part.getQuantityOnHand(), quantity));
        return true;
    }

    public boolean isPartAvailable(int partId, int quantity) {
        // Check if the part exists in the inventory
        Part part = partsById.get(partId);
        if (part == null) {
            return false;
        }
        return PartsInventoryEquations.isPartAvailable(part.getQuantityOnHand(), quantity);
    }

    public boolean areRequirementsAvailable(Iterable<PartRequirement> requirements) {
        // Check if any of the required parts are unavailable
        for (PartRequirement requirement : requirements) {
            if (!isPartAvailable(requirement.partId(), requirement.quantity())) {
                return true;
            }
        }
        return false;
    }

    public boolean fulfillRequirements(Iterable<PartRequirement> requirements) {
        // Check if all required parts are available before fulfilling the requirements
        if (areRequirementsAvailable(requirements)) {
            return false;
        }

        for (PartRequirement requirement : requirements) {
            if (!requestPart(requirement.partId(), requirement.quantity())) {
                return false;
            }
        }
        return true;
    }

    public boolean shouldReorder(Part part) {
        // Check if the part exists in the inventory
        if (part == null) {
            throw new IllegalArgumentException("Part cannot be null");
        }
        return PartsInventoryEquations.shouldReorder(
                part.getQuantityOnHand(), part.getReorderPoint());
    }

    public void receiveStock(int partId, int quantity) {
        //  Check if the part exists in the inventory
        Part part = partsById.get(partId);
        if (part == null) {
            throw new IllegalArgumentException("Unknown part id: " + partId);
        }
        part.setQuantityOnHand(
                PartsInventoryEquations.inventoryAfterReorder(part.getQuantityOnHand(), quantity));
    }
}
