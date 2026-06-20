package simulator.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import simulator.model.ServiceTicket;

public class PartsDelayMetrics {
    private final List<Double> partsDelays = new ArrayList<>();
    private double totalPartsDelay;
    private double totalJobDelay;
    private int recordedTickets;

    public void recordPartsDelay(ServiceTicket ticket) {
        partsDelays.add(ticket.getPartsDelay());
        totalPartsDelay += ticket.getPartsDelay();
        recordedTickets++;
    }

    public void recordTotalJobDelay(ServiceTicket ticket) {
        totalJobDelay += MetricsEquations.totalJobDelay(ticket.getQueueDelay(), ticket.getPartsDelay());
    }

    public double getAveragePartsDelay() {
        if (recordedTickets == 0) {
            return 0.0;
        }
        return totalPartsDelay / recordedTickets;
    }

    public double getAverageTotalJobDelay() {
        if (recordedTickets == 0) {
            return 0.0;
        }
        return totalJobDelay / recordedTickets;
    }

    public double getTotalPartsDelay() {
        return totalPartsDelay;
    }

    public List<Double> getPartsDelays() {
        return Collections.unmodifiableList(partsDelays);
    }
}
