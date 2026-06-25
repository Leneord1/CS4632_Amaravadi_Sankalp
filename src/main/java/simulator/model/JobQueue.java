package simulator.model;

import java.util.LinkedList;
import java.util.logging.Logger;

public class JobQueue {
    private static final Logger LOGGER = Logger.getLogger(JobQueue.class.getName());

    private final LinkedList<ServiceTicket> queue = new LinkedList<>();

    public void enqueue(ServiceTicket ticket) {
        queue.addLast(ticket);
    }

    public ServiceTicket dequeue() {
        if (queue.isEmpty()) {
            return null;
        }

        return queue.removeFirst();
    }

    public void printQueueDepth(String label) {
        LOGGER.info(String.format("[JobQueue] %s depth=%d", label, queue.size()));
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
