package simulator.inventory;

public record PendingPartOrder(
        int orderId, int partId, int quantity, double orderTimeHours, double arrivalTimeHours) {

    public boolean isDue(double currentTimeHours) {
        return currentTimeHours >= arrivalTimeHours;
    }
}
