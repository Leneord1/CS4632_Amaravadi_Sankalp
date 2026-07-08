package simulator.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simulator.model.Customer;
import simulator.model.RepairBay;
import simulator.model.ServiceTicket;
import simulator.model.Technician;
import simulator.model.TicketStatus;

class MetricsCollectorTest {
    @Test
    void buildReportAggregatesWaitAndUtilizationMetrics() {
        MetricsCollector collector = new MetricsCollector();
        collector.configureSimulation(10.0, 4.0, 3, 2.0);

        Customer customer = new Customer(0.5);
        ServiceTicket ticket = new ServiceTicket(1, "Oil Change", 0.3, 0.7);
        ticket.setQueueDelay(1.0);
        ticket.setPartsDelay(0.5);
        ticket.setStatus(TicketStatus.COMPLETE);

        Technician technician = new Technician(1, 4);
        RepairBay bay = new RepairBay(1);
        technician.setAssignedBay(bay);
        ticket.setAssignedTechnician(technician);

        customer.setTicket(ticket);
        collector.recordTicket(ticket);
        collector.recordCustomerCompletion(customer, 0.5, 2.0);

        MetricsReport report = collector.buildReport();

        assertEquals(4.0, report.getAverageCustomerWaitTime(), 1e-9);
        assertEquals(0.667, report.getAnalyticalSystemUtilization(), 1e-3);
        assertEquals(1, report.getJobsCompletedPerDay());
        assertEquals(0.07, report.getBayUtilization().get(bay), 1e-9);
        assertEquals(0.07, report.getSimulatedShopTechnicianUtilization(), 1e-9);
    }
}
