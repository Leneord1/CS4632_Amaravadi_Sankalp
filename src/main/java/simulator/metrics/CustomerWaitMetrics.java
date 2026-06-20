package simulator.metrics;

import simulator.model.Customer;
import simulator.model.ServiceTicket;

public class CustomerWaitMetrics {
    private double averageWaitTime;
    private double averageAdvisorWaitTime;
    private double averageQueueDelay;
    private double averagePartsDelay;
    private double averageServiceTime;
    private double averageTotalJobDelay;
    private int recordedCustomers;

    private double totalAdvisorWaitTime;
    private double totalQueueDelay;
    private double totalPartsDelay;
    private double totalServiceTime;
    private double totalCustomerWaitTime;
    private double totalJobDelay;

    public void recordCompletedCustomer(
            Customer customer,
            double advisorWaitTime,
            double serviceTime) {
        double queueDelay = 0.0;
        double partsDelay = 0.0;
        ServiceTicket ticket = customer.getTicket();
        if (ticket != null) {
            queueDelay = ticket.getQueueDelay();
            partsDelay = ticket.getPartsDelay();
        }

        double customerWaitTime = MetricsEquations.totalCustomerWaitTime(
                advisorWaitTime,
                queueDelay,
                partsDelay,
                serviceTime);
        double jobDelay = MetricsEquations.totalJobDelay(queueDelay, partsDelay);

        recordedCustomers++;
        totalAdvisorWaitTime += advisorWaitTime;
        totalQueueDelay += queueDelay;
        totalPartsDelay += partsDelay;
        totalServiceTime += serviceTime;
        totalCustomerWaitTime += customerWaitTime;
        totalJobDelay += jobDelay;
        customer.setTotalWaitTime(customerWaitTime);

        averageAdvisorWaitTime = totalAdvisorWaitTime / recordedCustomers;
        averageQueueDelay = totalQueueDelay / recordedCustomers;
        averagePartsDelay = totalPartsDelay / recordedCustomers;
        averageServiceTime = totalServiceTime / recordedCustomers;
        averageWaitTime = totalCustomerWaitTime / recordedCustomers;
        averageTotalJobDelay = totalJobDelay / recordedCustomers;
    }

    public void recordTicketDelays(ServiceTicket ticket) {
        totalQueueDelay += ticket.getQueueDelay();
        totalPartsDelay += ticket.getPartsDelay();
        totalJobDelay += MetricsEquations.totalJobDelay(ticket.getQueueDelay(), ticket.getPartsDelay());
    }

    public double getAverageWaitTime() {
        return averageWaitTime;
    }

    public double getAverageAdvisorWaitTime() {
        return averageAdvisorWaitTime;
    }

    public double getAverageQueueDelay() {
        return averageQueueDelay;
    }

    public double getAveragePartsDelay() {
        return averagePartsDelay;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public double getAverageTotalJobDelay() {
        return averageTotalJobDelay;
    }

    public int getRecordedCustomers() {
        return recordedCustomers;
    }
}
