package simulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobQueueTest {
    @Test
    void enqueueIncreasesSize() {
        JobQueue queue = new JobQueue();
        queue.enqueue(new ServiceTicket(1, "Oil Change", 0.3, 0.28));
        assertEquals(1, queue.size());
    }

    @Test
    void dequeueReturnsTicketsInFifoOrder() {
        JobQueue queue = new JobQueue();
        ServiceTicket first = new ServiceTicket(1, "Brakes: Front Brake Pad R&R, Rotor Resurface", 2.5, 2.3);
        ServiceTicket second = new ServiceTicket(2, "Wheel: 4 Tires R&R", 2.0, 1.5);
        queue.enqueue(first);
        queue.enqueue(second);

        assertEquals(first, queue.dequeue());
        assertEquals(second, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void dequeueOnEmptyQueueReturnsNull() {
        JobQueue queue = new JobQueue();
        assertNull(queue.dequeue());
    }

    @Test
    void isEmptyReflectsQueueState() {
        JobQueue queue = new JobQueue();
        assertTrue(queue.isEmpty());
        queue.enqueue(new ServiceTicket(3, "Alignment", 1.0, 0.5));
        assertFalse(queue.isEmpty());
    }
}
