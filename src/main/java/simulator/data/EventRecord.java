package simulator.data;

@SuppressWarnings("ALL")
public record EventRecord(
        long realEpochMillis, double simTimeHours, String eventType, int ticketId, String detail) {

    public static final String CSV_HEADER =
            "real_epoch_millis,sim_time_hours,event_type,ticket_id,detail";

    public String toCsvRow(String escapedDetail) {
        return realEpochMillis
                + ","
                + simTimeHours
                + ","
                + eventType
                + ","
                + ticketId
                + ","
                + escapedDetail;
    }
}
