package simulator.metrics;

import simulator.model.Customer;
import simulator.model.ServiceTicket;

public class CustomerWaitMetrics {
    private double averageWaitTime;
    private int recordedCustomers;

    public void recordCompletedCustomer(
            Customer customer,
            double advisorWaitTime,
            double serviceTime) {
    }

    public void recordTicketDelays(ServiceTicket ticket) {
    }

    public double getAverageWaitTime() {
        return averageWaitTime;
    }

    public int getRecordedCustomers() {
        return recordedCustomers;
    }
}
