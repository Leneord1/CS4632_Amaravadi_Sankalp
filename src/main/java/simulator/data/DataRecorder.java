package simulator.data;

import java.util.ArrayList;
import java.util.List;

// In-memory buffer for per-run time-series and event data.
// The engine appends here during a run; writers flush it to disk afterwards.
public class DataRecorder {
    private final List<TimeSeriesSample> samples = new ArrayList<>();
    private final List<EventRecord> events = new ArrayList<>();

    public void recordSample(TimeSeriesSample sample) {
        samples.add(sample);
    }

    public void recordEvent(EventRecord event) {
        events.add(event);
    }

    public List<TimeSeriesSample> getSamples() {
        return List.copyOf(samples);
    }

    public List<EventRecord> getEvents() {
        return List.copyOf(events);
    }

    public int getSampleCount() {
        return samples.size();
    }

    public int getEventCount() {
        return events.size();
    }
}
