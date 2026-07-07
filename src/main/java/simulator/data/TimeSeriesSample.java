package simulator.data;

public record TimeSeriesSample(
        long realEpochMillis,
        double simTimeHours,
        int intakeQueue,
        int jobQueueDepth,
        int busyTechnicians,
        int busyBays,
        int partsOnHand,
        int jobsCompleted) {

    public static String csvHeader() {
        return "real_epoch_millis,sim_time_hours,intake_queue,job_queue_depth,"
                + "busy_technicians,busy_bays,parts_on_hand,jobs_completed";
    }

    // Row order must match csvHeader().
    public String toCsvRow() {
        return realEpochMillis + ","
                + simTimeHours + ","
                + intakeQueue + ","
                + jobQueueDepth + ","
                + busyTechnicians + ","
                + busyBays + ","
                + partsOnHand + ","
                + jobsCompleted;
    }
}
