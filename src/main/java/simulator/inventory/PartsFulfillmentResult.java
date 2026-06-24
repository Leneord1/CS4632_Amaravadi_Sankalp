package simulator.inventory;

public record PartsFulfillmentResult(PartsFulfillmentStatus status, double partsDelayHours) {

    public boolean isFulfilled() {
        return status == PartsFulfillmentStatus.FULFILLED;
    }
}
