package simulator.model;

public record PartRequirement(int partId, int quantity) {
    public PartRequirement {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }
}
