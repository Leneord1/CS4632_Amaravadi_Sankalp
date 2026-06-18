package simulator.model;

import java.util.LinkedList;

public class JobQueue {
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

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}
