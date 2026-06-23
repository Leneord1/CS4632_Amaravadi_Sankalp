package simulator.inventory;

public class PartsFulfillmentResult {
    private final PartsFulfillmentStatus status;
    private final double partsDelayHours;

    public PartsFulfillmentResult(PartsFulfillmentStatus status, double partsDelayHours) {
        this.status = status;
        this.partsDelayHours = partsDelayHours;
    }

    public PartsFulfillmentStatus getStatus() {
        return status;
    }

    public double getPartsDelayHours() {
        return partsDelayHours;
    }

    public boolean isFulfilled() {
        return status == PartsFulfillmentStatus.FULFILLED;
    }
}
