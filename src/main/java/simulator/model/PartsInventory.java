package simulator.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import simulator.inventory.PartsInventoryEquations;

public class PartsInventory {
    private final Map<Integer, Part> partsById = new LinkedHashMap<>();

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
                PartsInventoryEquations.inventoryAfterFulfillment(part.getQuantityOnHand(), quantity));
        return true;
    }

    public boolean isPartAvailable(int partId, int quantity) {
        Part part = partsById.get(partId);
        if (part == null) {
            return false;
        }
        return PartsInventoryEquations.isPartAvailable(part.getQuantityOnHand(), quantity);
    }

    public boolean areRequirementsAvailable(Iterable<PartRequirement> requirements) {
        for (PartRequirement requirement : requirements) {
            if (!isPartAvailable(requirement.getPartId(), requirement.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    public boolean fulfillRequirements(Iterable<PartRequirement> requirements) {
        if (!areRequirementsAvailable(requirements)) {
            return false;
        }

        for (PartRequirement requirement : requirements) {
            if (!requestPart(requirement.getPartId(), requirement.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    public boolean shouldReorder(Part part) {
        return PartsInventoryEquations.shouldReorder(part.getQuantityOnHand(), part.getReorderPoint());
    }

    public void receiveStock(int partId, int quantity) {
        Part part = partsById.get(partId);
        if (part == null) {
            throw new IllegalArgumentException("Unknown part id: " + partId);
        }
        part.setQuantityOnHand(PartsInventoryEquations.inventoryAfterReorder(part.getQuantityOnHand(), quantity));
    }
}
