package simulator.data;

// One discrete event captured during a run (M3 event data).
// ticketId is -1 when the event is not tied to a ticket (e.g. parts arrival).
public record EventRecord(
        double simTimeHours,
        String eventType,
        int ticketId,
        String detail) {

    public static String csvHeader() {
        return "sim_time_hours,event_type,ticket_id,detail";
    }

    // Row order must match csvHeader(); detail is CSV-escaped by the writer.
    public String toCsvRow(String escapedDetail) {
        return simTimeHours + ","
                + eventType + ","
                + ticketId + ","
                + escapedDetail;
    }
}
