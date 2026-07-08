package simulator.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simulator.model.Customer;
import simulator.model.ServiceTicket;

class CustomerWaitMetricsTest {
    @Test
    void recordsWaitComponentsAndTotalCustomerWait() {
        CustomerWaitMetrics metrics = new CustomerWaitMetrics();
        Customer customer = new Customer(1.0);
        ServiceTicket ticket = new ServiceTicket(1, "Brakes", 2.0, 1.8);
        ticket.setQueueDelay(1.0);
        ticket.setPartsDelay(0.5);
        customer.setTicket(ticket);

        metrics.recordCompletedCustomer(customer, 0.5, 2.0);

        assertEquals(0.5, metrics.getAverageAdvisorWaitTime(), 1e-9);
        assertEquals(1.0, metrics.getAverageQueueDelay(), 1e-9);
        assertEquals(0.5, metrics.getAveragePartsDelay(), 1e-9);
        assertEquals(2.0, metrics.getAverageServiceTime(), 1e-9);
        assertEquals(4.0, metrics.getAverageWaitTime(), 1e-9);
        assertEquals(1.5, metrics.getAverageTotalJobDelay(), 1e-9);
        assertEquals(4.0, customer.getTotalWaitTime(), 1e-9);
    }
}
