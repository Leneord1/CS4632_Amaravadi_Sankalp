package simulator.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import simulator.model.ServiceTicket;

public class PartsDelayMetrics {
    private final List<Double> partsDelays = new ArrayList<>();
    private double totalPartsDelay;

    public void recordPartsDelay(ServiceTicket ticket) {
    }

    public void recordTotalJobDelay(ServiceTicket ticket) {
    }

    public double getAveragePartsDelay() {
        return 0.0;
    }

    public double getTotalPartsDelay() {
        return totalPartsDelay;
    }

    public List<Double> getPartsDelays() {
        return Collections.unmodifiableList(partsDelays);
    }
}
