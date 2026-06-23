package simulator.model;

public class PartRequirement {
    private final int partId;
    private final int quantity;

    public PartRequirement(int partId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        this.partId = partId;
        this.quantity = quantity;
    }

    public int getPartId() {
        return partId;
    }

    public int getQuantity() {
        return quantity;
    }
}
