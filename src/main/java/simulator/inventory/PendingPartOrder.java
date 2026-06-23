package simulator.inventory;

public class PendingPartOrder {
    private final int orderId;
    private final int partId;
    private final int quantity;
    private final double orderTimeHours;
    private final double arrivalTimeHours;

    public PendingPartOrder(
            int orderId,
            int partId,
            int quantity,
            double orderTimeHours,
            double arrivalTimeHours) {
        this.orderId = orderId;
        this.partId = partId;
        this.quantity = quantity;
        this.orderTimeHours = orderTimeHours;
        this.arrivalTimeHours = arrivalTimeHours;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getPartId() {
        return partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getOrderTimeHours() {
        return orderTimeHours;
    }

    public double getArrivalTimeHours() {
        return arrivalTimeHours;
    }

    public boolean isDue(double currentTimeHours) {
        return currentTimeHours >= arrivalTimeHours;
    }
}
