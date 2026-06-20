package simulator.metrics;

import org.junit.jupiter.api.Test;
import simulator.model.RepairBay;
import simulator.model.ServiceTicket;
import simulator.model.Technician;
import simulator.model.TicketStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TechnicianUtilizationMetricsTest {
    @Test
    void calculatesTechnicianAndShopUtilization() {
        TechnicianUtilizationMetrics metrics = new TechnicianUtilizationMetrics();
        metrics.setSimulationHorizonHours(10.0);

        Technician techOne = new Technician(1, 3);
        Technician techTwo = new Technician(2, 5);
        metrics.recordServiceTime(techOne, 5.0);
        metrics.recordServiceTime(techTwo, 3.0);

        assertEquals(0.5, metrics.getUtilization(techOne), 1e-9);
        assertEquals(0.3, metrics.getUtilization(techTwo), 1e-9);
        assertEquals(0.4, metrics.getShopTechnicianUtilization(), 1e-9);
    }

    @Test
    void completedJobUsesActualLaborTime() {
        TechnicianUtilizationMetrics metrics = new TechnicianUtilizationMetrics();
        metrics.setSimulationHorizonHours(8.0);

        Technician technician = new Technician(3, 4);
        RepairBay bay = new RepairBay(7);
        technician.setAssignedBay(bay);

        ServiceTicket ticket = new ServiceTicket(9, "Alignment", 1.0, 2.0);
        ticket.setStatus(TicketStatus.COMPLETE);
        metrics.recordCompletedJob(technician, ticket);

        assertEquals(0.25, metrics.getUtilization(technician), 1e-9);
        assertEquals(1, metrics.getJobsCompletedByTechnician(technician));
    }
}
