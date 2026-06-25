package simulator.metrics;

public class ThroughputMetrics {
    private int jobsCompletedPerDay;
    private int recordedJobs;

    public void recordJobCompletion() {
        recordedJobs++;
        jobsCompletedPerDay = recordedJobs;
    }

    public int getJobsCompletedPerDay() {
        return jobsCompletedPerDay;
    }

    public int getRecordedJobs() {
        return recordedJobs;
    }
}
