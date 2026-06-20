package simulator.metrics;

import simulator.model.ServiceTicket;

public class ThroughputMetrics {
    private int jobsCompletedPerDay;
    private int recordedJobs;

    public void recordJobCompletion(ServiceTicket ticket) {
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
