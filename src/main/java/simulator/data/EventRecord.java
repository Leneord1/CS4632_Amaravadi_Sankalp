package simulator.data;

@SuppressWarnings("ALL")
public record EventRecord(
        double simTimeHours,
        String eventType,
        int ticketId,
        String detail) {

    public static final String CSV_HEADER = "sim_time_hours,event_type,ticket_id,detail";

    public String toCsvRow(String escapedDetail) {
        return simTimeHours + ","
                + eventType + ","
                + ticketId + ","
                + escapedDetail;
    }
}
