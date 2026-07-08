package simulator.model;

import java.util.logging.Logger;

public class Part {
    private static final Logger LOGGER = Logger.getLogger(Part.class.getName());

    private final int partId;
    private final String description;
    private int quantityOnHand;
    private final int reorderPoint;
    private final int reorderQuantity;
    private final double meanLeadTimeHours;

    public Part(
            int partId,
            String description,
            int quantityOnHand,
            int reorderPoint,
            int reorderQuantity,
            double meanLeadTimeHours) {
        this.partId = partId;
        this.description = description;
        this.quantityOnHand = quantityOnHand;
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.meanLeadTimeHours = meanLeadTimeHours;
    }

    public void printAvailability(int requestedQuantity) {
        LOGGER.info(
                String.format(
                        "[Part %d] %s on-hand=%d requested=%d fillable=%b",
                        partId,
                        description,
                        quantityOnHand,
                        requestedQuantity,
                        quantityOnHand >= requestedQuantity));
    }

    public int getPartId() {
        return partId;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    public int getReorderQuantity() {
        return reorderQuantity;
    }

    public double getMeanLeadTimeHours() {
        return meanLeadTimeHours;
    }
}
